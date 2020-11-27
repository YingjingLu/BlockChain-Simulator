package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.json.simple.parser.ParseException;
public class DolevStrongRoundSimulator extends RoundSimulator {

    // total number of rounds to simulate
    // -1 if runs until terminating condition
    public final int totalRounds;
    public final DolevStrongJsonifier jsonifier;
    private final DolevStrongConfig config;

    DolevStrongPlayerController playerController;

    public DolevStrongRoundSimulator( final String traceRootPath)
            throws IOException, IllegalArgumentException, ParseException {
        super();
        jsonifier = new DolevStrongJsonifier(this, traceRootPath);
        this.config = jsonifier.getConfig();
        this.totalRounds = this.config.round;
        final int totalPlayer = this.config.numTotalPlayer;
        final int corruptPlayer = this.config.numCorruptPlayer;

        playerController = new DolevStrongPlayerController(
                networkSimulator,
                authenticator,
                honestPlayerMap,
                corruptPlayerMap
        );

        final int startCorrupt = totalPlayer - corruptPlayer;
        for (int i = 0; i < totalPlayer; i++) {
            if (i >= startCorrupt) {
                corruptPlayerMap.put(i, new DolevStrongPlayer(i, playerController));
            } else {
                honestPlayerMap.put(i, new DolevStrongPlayer(i, playerController));
            }
        }
    }

    public void run() throws IOException, IllegalArgumentException, ParseException {
        int initialRound = 0;
        final int sender = config.senderId;
        final Bit initialBit = config.inputBit;
        List<Bit> initialArray = new LinkedList<Bit>();
        initialArray.add(initialBit);
        final DolevStrongMessage initialMessage = new DolevStrongMessage(0, initialArray, -1, sender);
        playerController.beginRound(0);
        // give input to the player
        // start round 0
        // sender sends message to other players
        giveMessageToPlayer(sender, initialMessage, initialRound);
        playerController.sendInitialBitToOtherPlayersViaNetwork(sender);
        networkSimulator.sendMessagesToPlayers(initialRound);
        playerController.endRoundForPlayers(initialRound);

        for (int round = 1; round < totalRounds; round ++) {
            networkSimulator.sendMessagesToPlayers(round);
            playerController.beginRound(round);
            playerController.sendMessagesToOtherPlayersViaNetwork(round);
            playerController.endRoundForPlayers(round);
        }

        // the end of last round
        // every player reach an output
        networkSimulator.sendMessagesToPlayers(totalRounds);
        playerController.endRoundForPlayers(totalRounds);
        playerController.createOutputForEveryPlayer(totalRounds);
        playerController.printOutput();
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
