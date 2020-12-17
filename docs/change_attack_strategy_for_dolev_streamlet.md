# How to write an attack
Since our simulator is highly configurable, we have 2 ways to implement attacks. We will use Dolev Strong and Streamlet as examples.

## Approach 1: Change the Message Traces
This is the simplest approach to implement a fine-grained attack while not directly modifying any code. This is done by using manually configured `message_trace` to run the protocol as oppose to letting the simulator to generate message runs automatically. You can specify the messages and delay of those messages in each round and tell the simulator to use those messages as oppose to generating them:

1. In your `config.json` you can choose to turn on `use_trace` to be true. This allows the simulator to pick up the messages you specify in specific round as oppose to generate them itself. if `use_trace` is false, the simulator will ignore the messages you specify.

2. To configure messages in each round, using streamlet as an example, you can modify both `message_trace` and `proposal_trace`. The `proposal_trace` allows you to configure the block proposal in given round while the `message_trace` allows you to change proposal messages, vote messages and implicit message echoings including those for input, proposal, and vote. 

(Of course, for all those you can change, if you leave that field as blank, either some fields in a specific round or the entire file blank of a given run, the simulator will fill the messages needed according to what you have specified in previous rounds and according to the protocol's definition). For example, if you manually configure the messages for round 0, 2, 3 (`0.json`, `2.json`, `3.json` all exists and `1.json` does not exists in `message_trace` folder). The simulator will generate messages for round 1 according to what you have setup from round 0. Another example will be, if the `proposal_task` field in `2.json` does not exists, but round 2 needs to send proposal task according to protocol's definition, the simulator will fill in what proposal tasks should be generated for you.

## Approach 2: Change the code where adversary is implemented
This approach requires to change the code where adversary is implemented. Modifying the `PlayerController` for each protocol is enough as it is responsible to communicating with the players and generate all messages along with delay patameters. Methods that returns `List<Task>` to the round simulator are the key functions. They usually ask both the corrupt player method and honest player methods about what messages corrupt and honest players want to send, aggregrate them into a list of messages and return to `RoundSimulator`. The `RoundSimulator` will then forward those `Task` into `NetworkSimulator` to pass to the players in a given round specified by `delay` in each of the messages.

We use the `DolevStrongPlayerController` as an example: At round 0 the designated sender will need to send the input to other player. The below method will ask for corrupt player's way to generate message if the sender is corrupt, or honest way to generate message if the sender is honest.

```java
public List<Task> generatePlayerInputMessageList() {
    if (corruptPlayerMap.containsKey(senderId)) {
        return corruptPlayerSendInputToOtherPlayers(senderId);
    }
    else {
        return honestPlayerSendInputToOtherPlayers(senderId);
    }
}
```
So we can implement an attack that if the honest player needs to send the input bit to other players, it will divide the honest players into two groups and send one group the true input bit and the other the reverse bit.

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

## Builtin Native Attacks

Following up from the last section, there are some attack code built-in for both Dolev Strong and Streamlet. **Those attack strategies are not meant to be powerful attacks**. But rather, they serve two purposes:
1. when you add corrupt player into the protocol, you can notice there are some change in the message pattern. 
2. They serve as placeholder for future developers on where they can implement their attack on in the code.

**If you are looking for the vulnerable variant of the protocol, and attack on it as required from the project assignment, we direct you to the report's Vulnerable Variants section**
