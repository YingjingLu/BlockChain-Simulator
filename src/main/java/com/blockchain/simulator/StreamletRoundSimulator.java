package com.blockchain.simulator;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.json.simple.parser.ParseException;

public class StreamletRoundSimulator extends RoundSimulator {
    public final int totalRounds;
    public final StreamletJsonifier jsonifier;
    private final StreamletConfig config;

    StreamletPlayerController playerController;

    public StreamletRoundSimulator (final String configRootFolderPath)
            throws IOException, IllegalArgumentException, ParseException {
        super();
        jsonifier = new StreamletJsonifier(this, configRootFolderPath);
        this.config = jsonifier.getConfig();
        this.totalRounds = config.round;

        final int totalPlayer = config.numTotalPlayer;
        final int corruptPlayer = config.numCorruptPlayer;

        final int startCorrupt = totalPlayer - corruptPlayer;
        for (int i = 0; i < totalPlayer; i++) {
            if (i >= startCorrupt) {
                corruptPlayerMap.put(i, new StreamletPlayer(i, playerController));
            } else {
                honestPlayerMap.put(i, new StreamletPlayer(i, playerController));
            }
        }
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            playerMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            playerMap.put(entry.getKey(), entry.getValue());
        }

        playerController = new StreamletPlayerController(
                networkSimulator,
                authenticator,
                honestPlayerMap,
                corruptPlayerMap,
                playerMap
        );
    }

    public void run() throws IOException, IllegalArgumentException, ParseException {
        jsonifier.writeStateTracePath(-1);
        int curRound = 0;
        int curEpoch = 0;
        final int roundPerEpoch;
        if (config.maxDelay == -1) {
            roundPerEpoch = 2;
        } else {
            roundPerEpoch = 2 * config.maxDelay;
        }
        while (curRound < totalRounds) {
            networkSimulator.beginRound(curRound);
            // get all the messages already predefined in this round if user choose to use trace
            final StreamletMessageTrace roundMessageTrace;
            if (config.useTrace) {
                roundMessageTrace = jsonifier.getRoundMessageTrace(curRound);
            } else {
                roundMessageTrace = null;
            }

            // receive and process messages sent from previous round and process them
            playerController.sendInputMessagesToPlayers(this.config.inputMessageList.get(curRound));
            // broadcast inputs to the network
            final List<Task> echoInputTaskList;
            if (roundMessageTrace != null && roundMessageTrace.transactionEcho != null) {
                echoInputTaskList = roundMessageTrace.transactionEcho;
            } else {
                echoInputTaskList = playerController.generateInputEchoTaskList();
            }
            boundAndSubmitMessageToNetwork(curRound, echoInputTaskList);

            networkSimulator.sendMessagesToPlayers(curRound);

            // broadcast messages players receives
            final List<Task> echoMessageTaskList;
            if (roundMessageTrace != null && roundMessageTrace.messageEcho != null) {
                echoMessageTaskList = roundMessageTrace.messageEcho;
            } else {
                echoMessageTaskList = playerController.generateMessageEchoTaskList();
            }
            boundAndSubmitMessageToNetwork(curRound, echoMessageTaskList);

            // process those messages
            processMessagesReceivedForRound(curRound);

            // Message generation for this round begins
            final List<Task> blockProposalMessageCommunicationList;
            final List<Task> voteMessageList;

            // if this is the epoch start round, then try to propose block
            if (curRound % roundPerEpoch == 0) {
                final StreamletBlock proposedBlock;
                // start current round new block proposal
                final int leaderId;
                if (jsonifier.proposalExistsForRound(curRound)) {
                    proposedBlock = jsonifier.getRoundProposal(curRound);
                    leaderId = proposedBlock.getProposerId();
                } else {
                    leaderId = electLeader(curEpoch);
                    proposedBlock = playerController.proposeBlock(leaderId, curEpoch);
                }

                // generate the block as message to other players with delay (trace)
                if (roundMessageTrace != null && roundMessageTrace.proposalMessage != null) {
                    blockProposalMessageCommunicationList = roundMessageTrace.proposalMessage;
                } else {
                    blockProposalMessageCommunicationList = playerController.generateProposalMessageCommunicationList(
                            leaderId,
                            proposedBlock
                    );
                }
                boundAndSubmitMessageToNetwork(curRound, blockProposalMessageCommunicationList);
                // record the proposal back to the trace folder
                jsonifier.writeRoundProposal(curRound, proposedBlock);
                curEpoch ++;
            } else {
                blockProposalMessageCommunicationList = new LinkedList<>();
            }

            // send vote messages for any proposal received
            if (roundMessageTrace != null && roundMessageTrace.voteMessage != null) {
                voteMessageList = roundMessageTrace.voteMessage;
            } else {
                voteMessageList = playerController.generateVoteMessageList(curRound);
            }
            boundAndSubmitMessageToNetwork(curRound, voteMessageList);

            jsonifier.writeMessageTrace(
                    curRound,
                    blockProposalMessageCommunicationList,
                    voteMessageList,
                    echoInputTaskList,
                    echoMessageTaskList);

            jsonifier.writeStateTracePath(curRound);
            System.out.println("---------------------------");
            System.out.println("Round " + curRound);
            System.out.println("HonestPlayer: ");
            for(Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
                jsonifier.printPlayerState((StreamletPlayer) entry.getValue());
            }

            System.out.println("CorruptPlayer: ");
            for(Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
                jsonifier.printPlayerState((StreamletPlayer) entry.getValue());
            }

            curRound ++;
        }
    }

    public int electLeader(final int round) {
        return round % config.numTotalPlayer;
    }

    public void processMessagesReceivedForRound(final int curRound) {
        // start network simulator prepared for current round
        playerController.processInputs();
        playerController.processBlockProposal(curRound);
        playerController.processVotesForEachPlayer(curRound);
        playerController.finalizeChainForEachPlayer(curRound);
    }

    public void boundAndSubmitMessageToNetwork(final int curRound, final List<Task> taskList) {
        networkSimulator.boundMessageDelayForSynchronousNetwork(config.maxDelay, taskList);
        playerController.sendMessageListViaNetwork(curRound, taskList);
    }

}
