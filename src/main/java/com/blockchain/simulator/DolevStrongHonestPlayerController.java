package com.blockchain.simulator;

import java.util.Map;

public class DolevStrongHonestPlayerController extends PlayerController{

    public DolevStrongHonestPlayerController(
            final NetworkSimulator networkSimulator,
            final CryptographyAuthenticator authenticator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap) {
        super(networkSimulator, authenticator, honestPlayerMap, corruptPlayerMap);
    }

    public void beginRound(final int round) {
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer honestPlayer = (DolevStrongPlayer) entry.getValue();
            System.out.println("Cur Round size: " + honestPlayer.curRoundMessages.size());
            for (DolevStrongMessage srcMessage : honestPlayer.curRoundMessages) {
                assert srcMessage.getMessage().size() == 1 : "Message received should only contain one bit";
                assert srcMessage.getMessage().get(0) != Bit.FLOOR : "Message should not contain floor bit";
                honestPlayer.extractedSet.add(srcMessage.getMessage().get(0));
            }
            System.out.println("Round: " + round + "Player: " + honestPlayer.getId() + "ExtractedSet size: " + honestPlayer.extractedSet.size());
        }
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer player = (DolevStrongPlayer) entry.getValue();
            player.beginRound(round);
        }
    }

    public void sendInitialBitToOtherPlayersViaNetwork(final int senderId) {
        assert honestPlayerMap.containsKey(senderId): "Honest player map should contain the sender Id";
        DolevStrongPlayer sender = (DolevStrongPlayer) honestPlayerMap.get(senderId);
        assert sender.curRoundMessages.size() == 1 : "Sender should receive an initial bit of 1";
        DolevStrongMessage receivedMessage = sender.curRoundMessages.get(0);
        Bit messageBit = receivedMessage.getMessage().get(0);
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer destPlayer = (DolevStrongPlayer) entry.getValue();
            final DolevStrongMessage newMessage = (DolevStrongMessage) receivedMessage.deepCopy();
            newMessage.setFromPlayerId(sender.getId());
            newMessage.setToPlayerId(destPlayer.getId());
            authenticator.dolevStrongFAuth(newMessage);
            networkSimulator.sendMessage(destPlayer, newMessage, 0);
        }

        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer destPlayer = (DolevStrongPlayer) entry.getValue();
            final DolevStrongMessage newMessage = DolevStrongMessage.CreateMessageFromBit(
                    0, messageBit, sender.getId(), destPlayer.getId()
            );
            authenticator.dolevStrongFAuth(newMessage);
            networkSimulator.sendMessage(destPlayer, newMessage, 0);
        }
    }

    /**
     * Send the message as is to other players with its own signature
     *
     * @param round
     */
    public void sendMessagesToOtherPlayersViaNetwork(final int round) {
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final DolevStrongPlayer honestPlayer = (DolevStrongPlayer) entry.getValue();
            for (DolevStrongMessage srcMessage : honestPlayer.prevRoundMessages) {
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
                    networkSimulator.sendMessage(destPlayer, destMessage, 1);
                }

                for (Map.Entry<Integer, Player> destEntry : honestPlayerMap.entrySet()) {
                    final DolevStrongPlayer destPlayer = (DolevStrongPlayer) destEntry.getValue();
                    final DolevStrongMessage destMessage = (DolevStrongMessage) srcMessage.deepCopy();
                    destMessage.setRound(round);
                    destMessage.setFromPlayerId(honestPlayer.getId());
                    destMessage.setToPlayerId(destPlayer.getId());
                    authenticator.dolevStrongFAuth(destMessage);
                    networkSimulator.sendMessage(destPlayer, destMessage, 1);
                }
            }
        }
    }

    /**
     * honest nodes should scan all the messages received from current list and add new bits to the extracted set
     *
     * @param round
     */
    public void endRoundForPlayers(final int round) {

    }

    public void createOutputForEveryPlayer(final int round) {
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

    public void printOutput() {
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final Bit bit = ((DolevStrongPlayer)entry.getValue()).getOutputBit();
            System.out.println("Player : " + entry.getKey() + " output: " + bit.toString());
        }
    }
}
