package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;
public class DolevStrongRoundSimulator extends RoundSimulator {

    // total number of rounds to simulate
    // -1 if runs until terminating condition
    public final int totalRounds;
    private final DolevStrongConfig config;

    DolevStrongCorruptPlayerController corruptPlayerController;
    DolevStrongHonestPlayerController honestPlayerController;

    public DolevStrongRoundSimulator( DolevStrongConfig configuration) {
        super();
        this.totalRounds = configuration.round;
        this.config = configuration;
        final int totalPlayer = configuration.numTotalPlayer;
        final int corruptPlayer = configuration.numCorruptPlayer;

        corruptPlayerController = new DolevStrongCorruptPlayerController(
                networkSimulator,
                authenticator,
                honestPlayerMap,
                corruptPlayerMap
        );
        honestPlayerController = new DolevStrongHonestPlayerController(
                networkSimulator,
                authenticator,
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

    public void run() {
        int initialRound = 0;
        final int sender = config.senderId;
        final Bit initialBit = config.inputBit;
        List<Bit> initialArray = new LinkedList<Bit>();
        initialArray.add(initialBit);
        final DolevStrongMessage initialMessage = new DolevStrongMessage(0, initialArray, -1, sender);
        corruptPlayerController.beginRound(0);
        honestPlayerController.beginRound(0);
        // give input to the player
        // start round 0
        // sender sends message to other players
        giveMessageToPlayer(sender, initialMessage, initialRound);
        if (isPlayerHonest(sender)) {
            honestPlayerController.sendInitialBitToOtherPlayersViaNetwork(sender);
        } else {
            corruptPlayerController.sendInitialBitToOtherPlayersViaNetwork(sender);
        }
        networkSimulator.beginRound(initialRound);
        honestPlayerController.endRoundForPlayers(initialRound);
        corruptPlayerController.endRoundForPlayers(initialRound);

        for (int round = 1; round < totalRounds; round ++) {
            networkSimulator.beginRound(round);
            honestPlayerController.beginRound(round);
            corruptPlayerController.beginRound(round);
            honestPlayerController.sendMessagesToOtherPlayersViaNetwork(round);
            corruptPlayerController.sendMessagesToOtherPlayersViaNetwork(round);
            honestPlayerController.endRoundForPlayers(round);
            corruptPlayerController.endRoundForPlayers(round);
        }

        // the end of last round
        // every player reach an output
        networkSimulator.beginRound(totalRounds);
        honestPlayerController.endRoundForPlayers(totalRounds);
        corruptPlayerController.endRoundForPlayers(totalRounds);
        honestPlayerController.createOutputForEveryPlayer(totalRounds);
        corruptPlayerController.createOutputForEveryPlayer(totalRounds);
        honestPlayerController.printOutput();
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
