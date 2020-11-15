package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class StreamletHonestPlayerController extends PlayerController{

    public StreamletHonestPlayerController(
            final NetworkSimulator networkSimulator,
            final CryptographyAuthenticator authenticator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap) {
        super(networkSimulator, authenticator, honestPlayerMap, corruptPlayerMap);
    }

    /**
     * Iterate through all the tails, find the longest notorized chain and propose a new block with given round
     *
     * @param leaderId
     * @param curRound
     * @return
     */
    public StreamletBlock proposeBlock(final int leaderId, final int curRound, final List<Bit> message) {
        assert honestPlayerMap.containsKey(leaderId) : "elected leader passed to honest player controller should be honest";
        StreamletPlayer leader = (StreamletPlayer) honestPlayerMap.get(leaderId);
        int maxCurDepth = -1;
        StreamletBlock tailBlock = null;
        assert leader.chainTailMap.size() > 0 : "elected leader should have at least genesis block";
        for (Map.Entry<Integer, StreamletBlock> entry : leader.chainTailMap.entrySet()) {
            final StreamletBlock tail = entry.getValue();
            if (tail.getLevel() > maxCurDepth) {
                maxCurDepth = tail.getLevel();
                tailBlock = tail;
            }
        }
        assert tailBlock != null : "We should at least elect genesis block to extend";
        assert tailBlock.getNotorized() : "Elected block should be notorized";
        // create a new block that extends tail
        return new StreamletBlock(curRound, leader.getId(), message, tailBlock, tailBlock.getLevel() + 1);
    }

    public List<Task> generateProposalMessageCommunicationList(
            final int leaderId,
            final int curRound,
            final StreamletBlock block) {
        assert honestPlayerMap.containsKey(leaderId) : "calling generating proposal should provide with an honest leader";
        List<Task> taskList = new LinkedList<>();
        final StreamletPlayer srcPlayer = (StreamletPlayer) honestPlayerMap.get(leaderId);
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            addProposalMessageTask(srcPlayer, destPlayer, block, taskList);
        }
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            addProposalMessageTask(srcPlayer, destPlayer, block, taskList);
        }
        // convert the block into a message with cur block and prev block in the chain as signature
        return taskList;
    }

    public void addProposalMessageTask(
            final StreamletPlayer srcPlayer,
            final StreamletPlayer destPlayer,
            final StreamletBlock block,
            final List<Task> taskList) {

        final StreamletMessage newMessage = new StreamletMessage(
                false,
                block.getRound(),
                new LinkedList<Bit>(block.getMessage()),
                srcPlayer.getId(),
                destPlayer.getId(),
                block.getProposerId()
        );
        authenticator.streamletFAuth(newMessage);
        StreamletBlock prevBlock = block.getPrev();
        assert prevBlock != null : "Should not be genesis block to pass message";
        final String prevSign = authenticator.getStreamletFAuth(
                false,
                prevBlock.getRound(),
                "0",
                srcPlayer.getId(),
                destPlayer.getId(),
                prevBlock.getProposerId()
        );
        newMessage.addSignature(prevSign);
        final Task newTask = new Task(
                destPlayer,
                newMessage,
                0
        );
        taskList.add(newTask);
    }

    public void sendMessagesToOtherPlayersViaNetwork(final int round) {

    }

    public void endRoundForPlayers(final int round) {

    }
}
