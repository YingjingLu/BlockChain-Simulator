package com.blockchain.simulator;
import java.util.List;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.util.Map;

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

        final int startCorrupt = totalPlayer - corruptPlayer;
        for (int i = 0; i < totalPlayer; i++) {
            if (i >= startCorrupt) {
                corruptPlayerMap.put(i, new DolevStrongPlayer(i, playerController));
            } else {
                honestPlayerMap.put(i, new DolevStrongPlayer(i, playerController));
            }
        }
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            playerMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            playerMap.put(entry.getKey(), entry.getValue());
        }

        playerController = new DolevStrongPlayerController(
                networkSimulator,
                authenticator,
                honestPlayerMap,
                corruptPlayerMap,
                playerMap
        );
    }

    public void run() throws IOException, IllegalArgumentException, ParseException {
        jsonifier.writeStateTracePath(-1);
        int initialRound = 0;
        playerController.beginRound(initialRound);
        DolevStrongMessageTrace initialMessageTrace = null;
        if (config.useTrace) {
            initialMessageTrace = jsonifier.getRoundMessageTrace(initialRound);
        }
        final List<Task> initialTaskList;
        if (initialMessageTrace != null) {
            initialTaskList = initialMessageTrace.taskList;
        } else {
            playerController.sendInputMessagesToPlayers(config.inputMessageList.get(0));
            initialTaskList = playerController.generatePlayerInputMessageList();
        }
        playerController.sendMessageListViaNetwork(0, initialTaskList);
        networkSimulator.sendMessagesToPlayers(initialRound);
        playerController.endRoundForPlayers(initialRound);
        jsonifier.writeMessageTrace(0, initialTaskList);
        jsonifier.writeStateTracePath(0);

        for (int round = 1; round < totalRounds + 1; round ++) {
            networkSimulator.beginRound(round);
            networkSimulator.sendMessagesToPlayers(round);
            playerController.beginRound(round);
            final List<Task> taskList;
            final DolevStrongMessageTrace messageTrace;
            if (config.useTrace) {
                messageTrace = jsonifier.getRoundMessageTrace(round);
            } else {
                messageTrace = null;
            }
            if (messageTrace != null) {
                taskList = messageTrace.taskList;
            } else {
                taskList = playerController.generateMessageTasksAmongPlayers(round);
            }
            playerController.sendMessageListViaNetwork(round, taskList);
            playerController.endRoundForPlayers(round);
            jsonifier.writeMessageTrace(round, taskList);
            jsonifier.writeStateTracePath(round);
        }
        // the end of last round
        // every player reach an output
        playerController.createOutputForEveryPlayer(totalRounds);
        playerController.printOutput();
        jsonifier.writeOutput();
    }

}
