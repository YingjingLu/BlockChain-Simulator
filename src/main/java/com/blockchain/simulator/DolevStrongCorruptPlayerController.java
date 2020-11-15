package com.blockchain.simulator;

import java.util.Map;
public class DolevStrongCorruptPlayerController extends PlayerController{
    // record the negated bit
    // if the initial sender is corrupt should we use this parameter
    private Bit negatedBit = Bit.FLOOR;
    public DolevStrongCorruptPlayerController(
            final NetworkSimulator networkSimulator,
            final CryptographyAuthenticator authenticator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap) {
        super(networkSimulator, authenticator, honestPlayerMap, corruptPlayerMap);
    }

    public void beginRound(final int round) {
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer player = (DolevStrongPlayer) entry.getValue();
            player.beginRound(round);
        }
    }

    /**
     * Corrupt player's best strategy: send the egated bit to all dishonest plaers and send to half of honest players the
     * true bit and other half the negated bit
     * @param senderId
     */
    public void sendInitialBitToOtherPlayersViaNetwork(final int senderId) {
        assert corruptPlayerMap.containsKey(senderId): "Corrupt player map should contain the sender Id";
        DolevStrongPlayer sender = (DolevStrongPlayer) corruptPlayerMap.get(senderId);
        assert sender.curRoundMessages.size() == 1 : "Sender should receive an initial bit of 1";
        DolevStrongMessage receivedMessage = sender.curRoundMessages.get(0);
        Bit receivedBit = sender.curRoundMessages.get(0).getMessage().get(0);
        negatedBit = receivedBit.negateBit();
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer destPlayer = (DolevStrongPlayer) entry.getValue();
            final DolevStrongMessage newMessage = DolevStrongMessage.CreateMessageFromBit(
                    0, negatedBit, sender.getId(), destPlayer.getId()
            );
            authenticator.dolevStrongFAuth(newMessage);
            networkSimulator.sendMessage(destPlayer, newMessage, 0);
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
            networkSimulator.sendMessage(destPlayer, newMessage, 0);
        }


    }

    /**
     * Add their own signature to the message and send the copy of message to other player
     * For corrupt player the best way is to only send the messages with negated bit to other players
     * @param round
     */
    public void sendMessagesToOtherPlayersViaNetwork (final int round) {
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
                        networkSimulator.sendMessage(destPlayer, destMessage, 1);
                    }

                    for (Map.Entry<Integer, Player> destEntry : honestPlayerMap.entrySet()) {
                        final DolevStrongPlayer destPlayer = (DolevStrongPlayer) destEntry.getValue();
                        final DolevStrongMessage destMessage = (DolevStrongMessage) srcMessage.deepCopy();
                        destMessage.setRound(round);
                        destMessage.setFromPlayerId(corruptPlayer.getId());
                        destMessage.setToPlayerId(destPlayer.getId());
                        authenticator.dolevStrongFAuth(destMessage);
                        networkSimulator.sendMessage(destPlayer, destMessage, 1);
                    }
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
    }

    public void createOutputForEveryPlayer(final int round) {
        for (Map.Entry<Integer, Player> destEntry : corruptPlayerMap.entrySet()) {
            final DolevStrongPlayer player = (DolevStrongPlayer) destEntry.getValue();
            player.setOutputBit(negatedBit);
        }
    }

}
