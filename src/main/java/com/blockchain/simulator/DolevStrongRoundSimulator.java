package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;
public class DolevStrongRoundSimulator extends RoundSimulator {

    // total number of rounds to simulate
    // -1 if runs until terminating condition
    public final int totalRounds;

    DolevStrongCorruptPlayerController corruptPlayerController;
    DolevStrongHonestPlayerController honestPlayerController;

    public DolevStrongRoundSimulator(
            final int totalRounds,
            final int totalPlayer,
            final int corruptPlayer) {
        this.totalRounds = totalRounds;

        corruptPlayerController = new DolevStrongCorruptPlayerController(
                networkSimulator,
                honestPlayerMap,
                corruptPlayerMap
        );
        honestPlayerController = new DolevStrongHonestPlayerController(
                networkSimulator,
                honestPlayerMap,
                corruptPlayerMap
        );

        final int startCorrupt = totalPlayer - corruptPlayer;
        for (int i = 0; i < totalPlayer; i++) {
            if (i >= startCorrupt) {
                corruptPlayerMap.put(i, new DolevStrongPlayer(i, corruptPlayerController));
            } else {
                honestPlayerMap.put(i, new DolevStrongPlayer(i, honestPlayerController));
            }
        }
    }

    public int run() {
        int curRound = 0;
        final int sender = 0;
        final Bit initialBit = Bit.ONE;
        List<Bit> initialArray = new LinkedList<Bit>();
        initialArray.add(initialBit);
        final DolevStrongMessage initialMessage = new DolevStrongMessage(0, initialArray, -1, sender);
        corruptPlayerController.updateRound(0);
        honestPlayerController.updateRound(0);
        // give input to the player
        // start round 0
        // sender sends message to other players
        giveMessageToPlayer(sender, initialMessage, curRound);
        if (isPlayerHonest(sender)) {
            honestPlayerController.playerSendMessageToOtherPlayers(curRound);
        } else {
            corruptPlayerController.playerSendMessageToOtherPlayers(curRound);
        }

        for (int round = 1; round < totalRounds; round ++) {
            honestPlayerController.sendMessagesToOtherPlayersViaNetwork(curRound);
            corruptPlayerController.sendMessagesToOtherPlayersViaNetwork(curRound);
            networkSimulator.endRound(curRound);
            honestPlayerController.endRoundForPlayers(curRound);
            corruptPlayerController.endRoundForPlayers(curRound);
        }

        // the end of last round
        // every player reach an output
        honestPlayerController.createOutputForEveryPlayer(totalRounds);
        corruptPlayerController.createOutputForEveryPlayer(totalRounds);
        return 1;
    }

    private void giveMessageToPlayer(final int playerId, final DolevStrongMessage message, final int curRound) {
        DolevStrongPlayer player;
        if (honestPlayerMap.containsKey(playerId)) {
            player = (DolevStrongPlayer) honestPlayerMap.get(playerId);
        }
        else {
            player = (DolevStrongPlayer) corruptPlayerMap.get(playerId);
        }
        player.receiveMessage(message, curRound);
    }
}
