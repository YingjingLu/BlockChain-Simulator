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

    StreamletCorruptPlayerController corruptPlayerController;
    StreamletHonestPlayerController honestPlayerController;

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
                corruptPlayerMap.put(i, new StreamletPlayer(i, corruptPlayerController));
            } else {
                honestPlayerMap.put(i, new StreamletPlayer(i, honestPlayerController));
            }
        }

        corruptPlayerController = new StreamletCorruptPlayerController(
                networkSimulator,
                authenticator,
                honestPlayerMap,
                corruptPlayerMap
        );
        honestPlayerController = new StreamletHonestPlayerController(
                networkSimulator,
                authenticator,
                honestPlayerMap,
                corruptPlayerMap
        );
    }

    public void run() throws IOException, IllegalArgumentException, ParseException {
        jsonifier.writeStateTracePath(-1);
        for (int curRound = 0; curRound < totalRounds; curRound ++) {
            List<Bit> dummyMessage = new LinkedList<>();
            dummyMessage.add(Bit.ONE);

            final StreamletMessageTrace roundMessageTrace;
            if (config.useTrace) {
                roundMessageTrace = jsonifier.getRoundMessageTrace(curRound);
            } else {
                roundMessageTrace = null;
            }

            // start current round new block proposal
            final int leaderId;
            if (roundMessageTrace != null) {
                leaderId = roundMessageTrace.leader;
            } else {
                leaderId = electLeader(curRound);
            }
            // propose a leader with a block of a given round (trace)
            final StreamletBlock proposedBlock;
            if (roundMessageTrace != null && roundMessageTrace.proposal != null) {
                proposedBlock = roundMessageTrace.proposal;
                System.out.println("get block proposal " + proposedBlock.getRound() + " " + proposedBlock.getPrev().getRound());
            } else {
                proposedBlock = corruptPlayerController.proposeBlock(leaderId, curRound, dummyMessage);
            }
            assert proposedBlock != null : "proposedBlock cannot be null";
            assert proposedBlock.getPrev() != null : "proposed block has to have not null prev block";

            final List<Task> blockProposalMessageCommunicationList;
            // generate the block as message to other players with delay (trace)
            if (roundMessageTrace != null && roundMessageTrace.proposalMessage.size() > 0) {
                blockProposalMessageCommunicationList = roundMessageTrace.proposalMessage;
            } else {
                blockProposalMessageCommunicationList = corruptPlayerController.generateProposalMessageCommunicationList(
                        leaderId,
                        curRound,
                        proposedBlock
                );
            }
            // send the block to the network
            corruptPlayerController.sendMessageListViaNetwork(curRound, blockProposalMessageCommunicationList);
            // transact messages for the network for this round
            networkSimulator.beginRound(curRound);
            corruptPlayerController.processBlockProposal(curRound);
            // for those players received the proposed block,
            // process the message and generate the votes to other players (trace)
            final List<Task> voteMessageList;
            if (roundMessageTrace != null && roundMessageTrace.voteMessage.size() > 0) {
                voteMessageList = roundMessageTrace.voteMessage;
            } else {
                voteMessageList = corruptPlayerController.generateVoteMessageList(curRound, leaderId, proposedBlock);
            }
            jsonifier.writeMessageTrace(
                    leaderId,
                    curRound,
                    proposedBlock,
                    blockProposalMessageCommunicationList,
                    voteMessageList);
            // send vote to each other via network
            corruptPlayerController.sendMessageListViaNetwork(curRound, voteMessageList);
            // transact votes in the network of this round
            networkSimulator.beginRound(curRound);
            // process vote
            corruptPlayerController.processVotesForEachPlayer(curRound);
            corruptPlayerController.finalizeChainForEachPlayer(curRound);
            corruptPlayerController.endRoundForPlayers(curRound);

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
        }
    }

    public int electLeader(final int round) {
        return round % config.numTotalPlayer;
    }

}
