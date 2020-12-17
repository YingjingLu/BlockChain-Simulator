package com.blockchain.simulator;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;

public class DolevStrongPlayerController extends PlayerController{
    // record the negated bit
    // if the initial sender is corrupt should we use this parameter
    private Bit negatedBit = Bit.FLOOR;
    private int senderId;

    /**
     * Constructor
     * @param networkSimulator
     * @param authenticator
     * @param honestPlayerMap
     * @param corruptPlayerMap
     * @param playerMap
     */
    public DolevStrongPlayerController(
            final NetworkSimulator networkSimulator,
            final CryptographyAuthenticator authenticator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap,
            final Map<Integer, Player> playerMap) {
        super(networkSimulator, authenticator, honestPlayerMap, corruptPlayerMap, playerMap);
        senderId = -1;
    }

    /**
     * Begin round, process received messages for honest players, add received bits to extracted set for each
     * @param round
     */
    public void beginRound(final int round) {
        // corrupt player action
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer player = (DolevStrongPlayer) entry.getValue();
            player.beginRound(round);
        }
        // honest player action
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer honestPlayer = (DolevStrongPlayer) entry.getValue();
            for (DolevStrongMessage srcMessage : honestPlayer.curRoundMessages) {
                assert srcMessage.getMessage().size() == 1 : "Message received should only contain one bit";
                assert srcMessage.getMessage().get(0) != Bit.FLOOR : "Message should not contain floor bit";
                honestPlayer.extractedSet.add(srcMessage.getMessage().get(0));
            }
        }
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer player = (DolevStrongPlayer) entry.getValue();
            player.beginRound(round);
        }
    }

    /**
     * Method to send the initial input bit to other players
     * @param messageList
     */
    public void sendInputMessagesToPlayers(List<DolevStrongMessage> messageList) {
        for (DolevStrongMessage inputMessage : messageList) {
            DolevStrongPlayer targetPlayer = (DolevStrongPlayer) playerMap.get(inputMessage.getToPlayerId());
            targetPlayer.receiveInput(inputMessage);
            senderId = inputMessage.getToPlayerId();
        }
    }

    /**
     * Portal function that if the sender is corrupt then direct to corrupt player's strategy to send input
     * If honest direct to honest strategy to send input
     * @return
     */
    public List<Task> generatePlayerInputMessageList() {
        if (corruptPlayerMap.containsKey(senderId)) {
            return corruptPlayerSendInputToOtherPlayers(senderId);
        }
        else {
            return honestPlayerSendInputToOtherPlayers(senderId);
        }
    }

    /**
     * Corrupt player's strategy to send input currently implemented:
     * Divide the honest players into two groups(equally or almost equally), send the true input bit to one group
     * And the inverse bit to another, Always send inverse bit to other corrupt players
     *
     * @param senderId
     * @return
     */
    public List<Task> corruptPlayerSendInputToOtherPlayers(final int senderId) {
        final List<Task> taskList = new LinkedList<>();
        DolevStrongPlayer sender = (DolevStrongPlayer) corruptPlayerMap.get(senderId);
        // we only assume there is only one message for single bit Dolev Strong
        Bit receivedBit = sender.curRoundMessages.get(0).getMessage().get(0);
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

    /**
     * Honest players simply send the input bit along with its signature to other players
     * @param senderId
     * @return
     */
    public List<Task> honestPlayerSendInputToOtherPlayers(final int senderId) {
        final List<Task> taskList = new LinkedList<>();
        DolevStrongPlayer sender = (DolevStrongPlayer) honestPlayerMap.get(senderId);
        // we only assume there is only one message for single bit Dolev Strong
        DolevStrongMessage receivedMessage = sender.curRoundMessages.get(0);
        Bit messageBit = receivedMessage.getMessage().get(0);
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer destPlayer = (DolevStrongPlayer) entry.getValue();
            final DolevStrongMessage newMessage = (DolevStrongMessage) receivedMessage.deepCopy();
            newMessage.setFromPlayerId(sender.getId());
            newMessage.setToPlayerId(destPlayer.getId());
            authenticator.dolevStrongFAuth(newMessage);
            taskList.add(new Task(destPlayer, newMessage, 1));
        }

        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer destPlayer = (DolevStrongPlayer) entry.getValue();
            final DolevStrongMessage newMessage = DolevStrongMessage.CreateMessageFromBit(
                    0, messageBit, sender.getId(), destPlayer.getId()
            );
            authenticator.dolevStrongFAuth(newMessage);
            taskList.add(new Task(destPlayer, newMessage, 1));
        }
        return taskList;
    }

    /**
     * Add their own signature to the message and send the copy of message to other player,
     * This calls for corrupt and honest players to generate the messages they will be sending
     * @param round
     */
    public List<Task> generateMessageTasksAmongPlayers(final int round) {
        final List<Task> taskList = new LinkedList<>();
        corruptPlayerGenerateMessagesToOtherPlayers(round, taskList);
        honestPlayerGenerateMessageToOtherPlayers(round, taskList);
        return taskList;
    }

    /**
     * Corrupt player's strategy to send messages to other players: only send those with the inverse bit of the input
     * bit to other corrupt players.
     * @param round
     * @param taskList
     */
    public void corruptPlayerGenerateMessagesToOtherPlayers(final int round, final List<Task> taskList) {
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer corruptPlayer = (DolevStrongPlayer) entry.getValue();
            for (DolevStrongMessage srcMessage : corruptPlayer.curRoundMessages) {
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

    /**
     * Honest players simply add the signature to the message and send it to other players
     * @param round
     * @param taskList
     */
    public void honestPlayerGenerateMessageToOtherPlayers(final int round, final List<Task> taskList) {
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer honestPlayer = (DolevStrongPlayer) entry.getValue();
            for (DolevStrongMessage srcMessage : honestPlayer.curRoundMessages) {
                assert srcMessage.getMessage().size() == 1 : "Message received should only contain one bit";
                // send message to pther players except to itself
                // send to other corrupt players
                for (Map.Entry<Integer, Player> destEntry : corruptPlayerMap.entrySet()) {
                    final DolevStrongPlayer destPlayer = (DolevStrongPlayer) destEntry.getValue();
                    final DolevStrongMessage destMessage = (DolevStrongMessage) srcMessage.deepCopy();
                    destMessage.setRound(round);
                    destMessage.setFromPlayerId(honestPlayer.getId());
                    destMessage.setToPlayerId(destPlayer.getId());
                    authenticator.dolevStrongFAuth(destMessage);
                    taskList.add(new Task(destPlayer, destMessage, 1));
                }

                for (Map.Entry<Integer, Player> destEntry : honestPlayerMap.entrySet()) {
                    final DolevStrongPlayer destPlayer = (DolevStrongPlayer) destEntry.getValue();
                    final DolevStrongMessage destMessage = (DolevStrongMessage) srcMessage.deepCopy();
                    destMessage.setRound(round);
                    destMessage.setFromPlayerId(honestPlayer.getId());
                    destMessage.setToPlayerId(destPlayer.getId());
                    authenticator.dolevStrongFAuth(destMessage);
                    taskList.add(new Task(destPlayer, destMessage, 1));
                }
            }
        }
    }

    /**
     * Corrupt players do not have to reach any conclusion at the end of the round
     *
     * @param round
     */
    public void endRoundForPlayers(final int round) {
        for (Map.Entry<Integer, Player> entry : playerMap.entrySet()) {
            ((DolevStrongPlayer)entry.getValue()).endRound();
        }
    }

    /**
     * Create output for honest players. Per Dolev strong, 
     * if nothing in extracted set or both bits in extracted set set output FLOOR bit
     * 
     * If there is only one bit, set to that bit
     *
     * @param round
     */
    public void createOutputForEveryPlayer(final int round) {
        // corrupt player output
        for (Map.Entry<Integer, Player> destEntry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer player = (DolevStrongPlayer) destEntry.getValue();
            player.setOutputBit(negatedBit);
        }
        // honest player output
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer honestPlayer = (DolevStrongPlayer) entry.getValue();
            if (honestPlayer.extractedSet.size() == 1) {
                if (honestPlayer.extractedSet.contains(Bit.ZERO)) {
                    honestPlayer.setOutputBit(Bit.ZERO);
                } else if (honestPlayer.extractedSet.contains(Bit.ONE)) {
                    honestPlayer.setOutputBit(Bit.ONE);
                }
            } else {
                honestPlayer.setOutputBit(Bit.FLOOR);
            }
        }
    }

    /**
     * Print honest players' output to stdout
     */
    public void printOutput() {
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final Bit bit = ((DolevStrongPlayer)entry.getValue()).getOutputBit();
            System.out.println("Player : " + entry.getKey() + " output: " + bit.toString());
        }
    }
}
