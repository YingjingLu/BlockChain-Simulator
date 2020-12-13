# How to write an attack
Since our simulator is highly configurable, we have 2 ways to implement attachs. We will use Dolev Strong and Streamlet as examples.

## Approach 1: Change the Message Traces
This is the simplest approach to implement a fine-grained attack while not directly modifying any code. 

As we have mentioned in `config_and_trace` doc, the model comfig can specify whether to use messages specified in `message_trace` to run the protocol or to generate messages when running the simulator. So you can write out what messages to be transmitted at each round manually and let the simulator to execute that, you can implement an attack without changing the code because the simulator will use the messages you wrote to run the protocol.

1. In your `config.json` you can choose to turn on `use_trace` to be true. This allows the simulator to pick up the messages you specify in specific round as oppose to generate it itself.

2. To change the messages generated, using streamlet as an example, you can specify a basic `config.json` and let the simulator to generate all the message traces, and modify them later. In both `message_trace` and `proposal_trace` folders you can change any of the proposal, change the delay parameter or delete one message object(for dropping that message), or any other parameters you want. As long as your change will align with the protocol's definition.

3. Run the protocol again with those message traces and you will get the results written in `player_state_traces` for the player states changes for each round as the output. 

## Approach 2: Change the code where adversary is implemented
This approach requires to change the code where adversary is implemented. YOu can look at the `StreamletPlayerController` which is responsible to communicate with players to generate messages according to their states. You can do any modifications you want, just follow the output format to output the message tasks with delays so that our `RoundSimulator` will pass those to the `NetworkSimulator` to send to players.

Using Dolev strong as a more detailed example, You can change the code in the `DolevStrongPlayerController` on how to generate messages for attacks.

For example we defined how corrupt players send input to other players in which our current attack strategy is to divide honest players into equally two groups and send one group the bit of input as is, and another group with the inversed bit. Then according to this strategy you construct the messahe tasks and return it to the round simulator to be sent through the network. Code snippet:

```java
public List<Task> corruptPlayerSendInputToOtherPlayers(final int senderId) {
        final List<Task> taskList = new LinkedList<>();
        DolevStrongPlayer sender = (DolevStrongPlayer) corruptPlayerMap.get(senderId);
        assert sender.curRoundInputMessages.size() == 1 : "Sender should receive an initial bit of 1";
        Bit receivedBit = sender.curRoundInputMessages.get(0).getMessage().get(0);
        negatedBit = receivedBit.negateBit();
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer destPlayer = (DolevStrongPlayer) entry.getValue();
            final DolevStrongMessage newMessage = DolevStrongMessage.CreateMessageFromBit(
                    0, negatedBit, sender.getId(), destPlayer.getId()
            );
            authenticator.dolevStrongFAuth(newMessage);
            taskList.add(new Task(destPlayer, newMessage, 1));
        }
        final int honestPlayerCount = honestPlayerMap.size();
        final int half = honestPlayerCount / 2;
        int cur = 0;
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer destPlayer = (DolevStrongPlayer) entry.getValue();
            DolevStrongMessage newMessage;
            if (cur < half) {
                newMessage = DolevStrongMessage.CreateMessageFromBit(
                        0, negatedBit, sender.getId(), destPlayer.getId()
                );
            } else {
                newMessage = DolevStrongMessage.CreateMessageFromBit(
                        0, receivedBit, sender.getId(), destPlayer.getId()
                );
            }
            cur ++;
            authenticator.dolevStrongFAuth(newMessage);
            taskList.add(new Task(destPlayer, newMessage, 1));
        }
        return taskList;
    }
```

Here below we implemented how corrupt players send the messages they receive to other players. In our strategy corrupt players only forward the messages received from other corrupt players to honest players. Code snippet:

```java
public void corruptPlayerGenerateMessagesToOtherPlayers(final int round, final List<Task> taskList) {
    for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
        final DolevStrongPlayer corruptPlayer = (DolevStrongPlayer) entry.getValue();
        for (DolevStrongMessage srcMessage : corruptPlayer.prevRoundMessages) {
            assert srcMessage.getMessage().size() == 1 : "Message received should only contain one bit";
            if (srcMessage.getMessage().get(0) == negatedBit) {
                // send message to pther players except to itself
                // send to other corrupt players
                for (Map.Entry<Integer, Player> destEntry : corruptPlayerMap.entrySet()) {
                    final DolevStrongPlayer destPlayer = (DolevStrongPlayer) destEntry.getValue();
                    final DolevStrongMessage destMessage = (DolevStrongMessage) srcMessage.deepCopy();
                    destMessage.setRound(round);
                    destMessage.setFromPlayerId(corruptPlayer.getId());
                    destMessage.setToPlayerId(destPlayer.getId());
                    authenticator.dolevStrongFAuth(destMessage);
                    taskList.add(new Task(destPlayer, destMessage, 1));
                }

                for (Map.Entry<Integer, Player> destEntry : honestPlayerMap.entrySet()) {
                    final DolevStrongPlayer destPlayer = (DolevStrongPlayer) destEntry.getValue();
                    final DolevStrongMessage destMessage = (DolevStrongMessage) srcMessage.deepCopy();
                    destMessage.setRound(round);
                    destMessage.setFromPlayerId(corruptPlayer.getId());
                    destMessage.setToPlayerId(destPlayer.getId());
                    authenticator.dolevStrongFAuth(destMessage);
                    taskList.add(new Task(destPlayer, destMessage, 1));
                }
            }
        }
    }
    }
```

## Builtin Native Attacks

Following up from the last section, there are some attack code built-in for both Dolev Strong and Streamlet. **Those attack strategies are not meant to be powerful attacks**. But rather, it serves two purposes:
1. when you add corrupt player into the protocol, you can notice there are some change in the message pattern. 
2. They serve as placeholder for furute developers on where they can implement their attack on in the code.

**If you are looking for the vulnerable variant of the protocol, and attack on it as required from the project assignment, we direct you to the report's <Vulnerable Variants> section**
