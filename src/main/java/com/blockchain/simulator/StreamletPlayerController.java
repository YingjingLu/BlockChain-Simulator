package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.lang.Math;
import java.util.Iterator;

public class StreamletPlayerController extends PlayerController {
    public List<Integer> honestPlayerIdList;
    public List<Integer> corruptPlayerIdList;

    public Set<Integer> blockRoundProposedByCorruptPlayerSet;
    public List<Integer> honestG1;
    public List<Integer> honestG2;

    public StreamletPlayerController(
            final NetworkSimulator networkSimulator,
            final CryptographyAuthenticator authenticator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap,
            final Map<Integer, Player> playerMap) {
        super(networkSimulator, authenticator, honestPlayerMap, corruptPlayerMap, playerMap);
        corruptPlayerIdList = new LinkedList<>();
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            corruptPlayerIdList.add(entry.getKey());
        }

        blockRoundProposedByCorruptPlayerSet = new HashSet<>();
        // divide honest players into two groups
        honestPlayerIdList = new LinkedList<>();
        honestG1 = new LinkedList<>();
        honestG2 = new LinkedList<>();
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            honestPlayerIdList.add(entry.getKey());
        }
        Collections.sort(honestPlayerIdList);
        int half = honestPlayerIdList.size() / 2;
        for (int id : honestPlayerIdList) {
            if (half > 0) {
                honestG1.add(id);
                half --;
            } else {
                honestG2.add(id);
            }
        }
    }

    public void sendInputMessagesToPlayers(List<StreamletMessage> inputMessageList) {
        for (StreamletMessage message : inputMessageList) {
            StreamletPlayer targetPlayer = (StreamletPlayer) playerMap.get(message.getToPlayerId());
            targetPlayer.receiveInput(message);
        }
    }

    /**
     * Iterate through each player's received input for this round, generate their broadcast to each player
     * each player then broadcast this info to other players
     *
     * TODO: Currently there is no adversary to interfere in the middle, but we can implement according to attach strategy
     * @custom.adversary_dependent: We can implement adversary to block echoing transmission
     * @return
     */
    public List<Task> generateInputEchoTaskList() {
        List<Task> res = new LinkedList<>();
        for (Map.Entry<Integer, Player> entry : playerMap.entrySet()) {
            StreamletPlayer initialPlayer = (StreamletPlayer) entry.getValue();
            for (StreamletMessage inputMessage : initialPlayer.curRoundInputMessageList) {
                // initial message should first send to every one
                for (Map.Entry<Integer, Player> innerEntry : playerMap.entrySet()) {
                    Player firstHandPlayer = innerEntry.getValue();
                    StreamletMessage newMessage = (StreamletMessage) inputMessage.deepCopy();
                    newMessage.setFromPlayerId(initialPlayer.getId());
                    newMessage.setToPlayerId(firstHandPlayer.getId());
                    res.add(new Task(firstHandPlayer, newMessage, 1));
                }
                // Since we have no delay, every player just directly echo this message back to others
                // if we will have delay version we need to re-implement this
                for (Map.Entry<Integer, Player> srcEntry : playerMap.entrySet()) {
                    Player broadcastSrcPlayer = srcEntry.getValue();
                    for (Map.Entry<Integer, Player> targetEntry : playerMap.entrySet()) {
                        Player broadcastTargetPlayer = targetEntry.getValue();
                        StreamletMessage newMessage = (StreamletMessage) inputMessage.deepCopy();
                        newMessage.setFromPlayerId(broadcastSrcPlayer.getId());
                        newMessage.setToPlayerId(broadcastTargetPlayer.getId());
                        res.add(new Task(broadcastTargetPlayer, newMessage, 1));
                    }
                }
            }
        }
        return res;
    }

    /**
     * Iterate through every player's received message list in this round, broadcast them to other players
     * Currently default to let them receive it in the next round, but may subject to adversary attack
     * @custom.adversary_dependent: we can add adversary attack on this to block echoing
     * @return
     */
    public List<Task> generateMessageEchoTaskList() {
        List<Task> result = new LinkedList<>();
        for (Map.Entry<Integer, Player> entry : playerMap.entrySet()) {
            StreamletPlayer player = (StreamletPlayer) entry.getValue();
            for (StreamletMessage streamletMessage : player.curRoundMessageList) {
                for (Map.Entry<Integer, Player> innerEntry : playerMap.entrySet()) {
                    Message messageCopy = streamletMessage.deepCopy();
                    Task messageTask = new Task(innerEntry.getValue(), messageCopy, 1);
                    result.add(messageTask);
                }
            }
        }
        return result;
    }

    public void processInputs() {
        for (Map.Entry<Integer, Player> entry : playerMap.entrySet()) {
            StreamletPlayer player = (StreamletPlayer) entry.getValue();
            player.proceeeInputs();
        }
    }

    public StreamletBlock proposeBlock(final int leaderId, final int curEpoch) {
        if (honestPlayerMap.containsKey(leaderId)) {
            return honestPlayerProposeBlock(leaderId, curEpoch);
        } else {
            return corruptPlayerProposeBlock(leaderId, curEpoch);
        }
    }

    /**
     * Honest player's proposal strategy:
     * look into the view of the honest player, choose to extend from the longest notarized chain
     *
     * @param leaderId
     * @param curEpoch
     * @return
     */
    public StreamletBlock honestPlayerProposeBlock(final int leaderId, final int curEpoch) {
        StreamletPlayer leader = (StreamletPlayer) honestPlayerMap.get(leaderId);
        int maxCurDepth = -1;
        StreamletBlock tailBlock = null;
        assert leader.chainTailMap.size() > 0 : "elected leader should have at least genesis block";
        final List<Integer> message = new LinkedList<>();
        // honest player always incorporate all the info it receives into the message
        Iterator<Integer> txIterator = leader.pendingTransactionSet.iterator();
        while (txIterator.hasNext()) {
            message.add(txIterator.next());
        }

        /**
         * Iterate through every tail
         * trace through the chain to find the most recent notarized block, record its depth
         */
        for (Map.Entry<Integer, StreamletBlock> entry : leader.chainTailMap.entrySet()) {
            StreamletBlock tail = entry.getValue();
            while (tail != null && !tail.getNotorized()) {
                tail = tail.getPrev();
            }
            assert tail != null : "trace should not pass beyond genesis block";
            if (tail.getLevel() > maxCurDepth) {
                maxCurDepth = tail.getLevel();
                tailBlock = tail;
            }
        }
        assert tailBlock != null : "We should at least elect genesis block to extend";
        assert tailBlock.getNotorized() : "Elected block should be notorized";
        // create a new block that extends tail
        return new StreamletBlock(curEpoch, leader.getId(), message, tailBlock, tailBlock.getLevel() + 1);
    }

    /**
     * Corrupt player has the following strategy:
     *      * Assume that it has at least 1/3 of them, and corrupt players are elected as leaders in consecutive rounds:
     *      * divide the honest players into two groups, try to make the two groups of honest players of different view
     *      * for certain rounds.
     *      *
     *      * Strategy:
     *      * if the last round was proposed by corrupt player and that block gets notarized
     *      *
     *      * if there are two notorized chains of same length
     *      *     propose from one arbitrarily, prioritize the one that has tail notorized block proposed by corrupt player
     *      * else:
     *      *     propose a block that extends the second longest chain
     *
     * @param leaderId
     * @param curEpoch
     * @return
     */
    public StreamletBlock corruptPlayerProposeBlock(final int leaderId, final int curEpoch) {
        StreamletPlayer leader = (StreamletPlayer) corruptPlayerMap.get(leaderId);
        // maintain a block list of size 2 (cur second block, cur first block)
        LinkedList<StreamletBlock> size2Heap = new LinkedList<>();
        assert leader.chainTailMap.size() > 0 : "elected leader should have at least genesis block";

        // currently corrupt players also incorporate all the incoming messages in its block
        final List<Integer> message = new LinkedList<>();
        // honest player always incorporate all the info it receives into the message
        Iterator<Integer> txIterator = leader.pendingTransactionSet.iterator();
        while (txIterator.hasNext()) {
            message.add(txIterator.next());
        }
        // if the last round is proposed by corrupt player, gets notorized, then extend it
        final int lastBlockRound = curEpoch - 1;
        if (blockRoundProposedByCorruptPlayerSet.contains(lastBlockRound)) {
            if (leader.blockMap.get(lastBlockRound).getNotorized()) {
                blockRoundProposedByCorruptPlayerSet.add(curEpoch);
                // create a new block that extends tail
                return new StreamletBlock(
                        curEpoch,
                        leader.getId(),
                        message,
                        leader.blockMap.get(lastBlockRound),
                        leader.blockMap.get(lastBlockRound).getLevel() + 1);
            }
        }
        /**
         * Otherwise
         * Iterate through every tail
         * trace through the chain to find the most recent notarized block, record its depth
         */
        for (Map.Entry<Integer, StreamletBlock> entry : leader.chainTailMap.entrySet()) {
            StreamletBlock tail = entry.getValue();
            while (tail != null && !tail.getNotorized()) {
                tail = tail.getPrev();
            }
            assert tail != null : "trace should not pass beyond genesis block";
            if (size2Heap.size() == 0) {
                size2Heap.add(tail);
            } else if (size2Heap.size() == 1) {
                if (size2Heap.getLast().getLevel() < tail.getLevel()) {
                    size2Heap.addLast(tail);
                } else {
                    size2Heap.addFirst(tail);
                }
            } else {
                if (size2Heap.getLast().getLevel() < tail.getLevel()) {
                    size2Heap.addLast(tail);
                    size2Heap.removeFirst();
                } else if (tail.getLevel() <= size2Heap.getLast().getLevel() &&
                           tail.getLevel() > size2Heap.getFirst().getLevel()) {
                    size2Heap.removeLast();
                    size2Heap.addFirst(tail);
                }
            }
        }
        assert size2Heap.size() > 0 : "We should at least get one notarized chain";
        StreamletBlock secondDeepBlock = size2Heap.getFirst();;
        // if the two longest chain has the same length, try to priortize the one ends with corrupt player's proposal
        if (size2Heap.size() == 2 && size2Heap.getFirst().getLevel() == size2Heap.getLast().getLevel()) {
            if (blockRoundProposedByCorruptPlayerSet.contains(size2Heap.getLast().getEpoch())) {
                secondDeepBlock = size2Heap.getLast();
            }
        }
        // add this proposal to the corrupt propose set record
        blockRoundProposedByCorruptPlayerSet.add(curEpoch);
        // create a new block that extends tail
        return new StreamletBlock(
                curEpoch,
                leader.getId(),
                message,
                secondDeepBlock,
                secondDeepBlock.getLevel() + 1);
    }

    /**
     * Send the block proposal to all other players, our attack do not prevent other players
     * from getting a block proposal
     *
     * @param leaderId
     * @param block
     * @return
     */
    public List<Task> generateProposalMessageCommunicationList(
            final int leaderId,
            final StreamletBlock block) {
        List<Task> taskList = new LinkedList<>();
        final StreamletPlayer srcPlayer;
        if (honestPlayerMap.containsKey(leaderId)) {
            srcPlayer = (StreamletPlayer) honestPlayerMap.get(leaderId);
        } else {
            assert corruptPlayerMap.containsKey(leaderId) : "leader id shuld be a valid leader id";
            srcPlayer = (StreamletPlayer) corruptPlayerMap.get(leaderId);
        }

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
                block.getEpoch(),
                new LinkedList<>(block.getMessage()),
                srcPlayer.getId(),
                destPlayer.getId(),
                block.getProposerId()
        );
        authenticator.streamletFAuth(newMessage);
        StreamletBlock prevBlock = block.getPrev();
        assert prevBlock != null : "Should not be genesis block to pass message";
        final String prevSign = authenticator.getStreamletFAuth(
                false,
                prevBlock.getEpoch(),
                "0",
                srcPlayer.getId(),
                destPlayer.getId(),
                prevBlock.getProposerId()
        );
        newMessage.addSignature(prevSign);
        final Task newTask = new Task(
                destPlayer,
                newMessage,
                1
        );
        taskList.add(newTask);
    }

    /**
     * Core step during the attack. Our strategy is follow:
     * Divide the honest players into two groups called G1 and G2:
     *
     * corrupt players will vote for all proposals
     * When player in G1 propose a block, block all votes from G2 or to G2 for f+1 rounds
     * When player in G2 propose a block, block all votes from G1 or to G1 for f+1 rounds
     * Do not block votes otherwise
     *
     * @param curRound
     * @return
     */
    public List<Task> generateVoteMessageList(final int curRound) {
        List<Task> res = new LinkedList<>();
        final int attackDelay = corruptPlayerMap.size() + honestPlayerMap.size() + 1;
        // if leader is corrupt send to all other nodes without delay
        for (int fromId : corruptPlayerIdList) {
            generateVoteMessages(res, fromId, corruptPlayerIdList, 1);
            generateVoteMessages(res, fromId, honestPlayerIdList, 1);
        }
        for (int fromId : honestG1) {
            generateVoteMessages(res, fromId, corruptPlayerIdList, 1);
            generateVoteMessages(res, fromId, honestG1, 1);
//            generateVoteMessages(res, fromId, honestG2, block, attackDelay);
            generateVoteMessages(res, fromId, honestG2, 1);
        }
        for (int fromId : honestG2) {
            generateVoteMessages(res, fromId, corruptPlayerIdList, 1);
            generateVoteMessages(res, fromId, honestG2, 1);
//            generateVoteMessages(res, fromId, honestG1, block, attackDelay);
            generateVoteMessages(res, fromId, honestG1, 1);
        }
        return res;
    }

    /**
     * Obtain the vote for fromPlayerId, send this vote to all other players in the toPlaerIdList with given delay
     * Add them into voteMessageList
     *
     * @param voteMessageList
     * @param fromPlayerId
     * @param toPlayerIdList
     * @param delay
     */
    public void generateVoteMessages(
            final List<Task> voteMessageList,
            final int fromPlayerId,
            final List<Integer> toPlayerIdList,
            final int delay) {
        final StreamletPlayer player;
        final boolean playerHonest;

        if (corruptPlayerMap.containsKey(fromPlayerId)) {
            player = (StreamletPlayer) corruptPlayerMap.get(fromPlayerId);
            playerHonest = false;
        } else {
            player = (StreamletPlayer) honestPlayerMap.get(fromPlayerId);
            playerHonest = true;
        }
        for (StreamletBlock block : player.blockPendingVotingForCurRound) {
            final boolean decision;
            // honest player determine vote according to protocol
            // corrupt player always vote agreed according to current attack
            if (playerHonest) {
                decision = player.determineBlockVote(block);
            } else {
                decision = true;
            }
            for (int toPlayerId : toPlayerIdList) {
                StreamletMessage newMessage = new StreamletMessage(
                        true,
                        block.getEpoch(),
                        block.getMessage(),
                        fromPlayerId,
                        toPlayerId,
                        block.getProposerId()
                );
                if (decision) {
                    newMessage.setApprove();
                } else {
                    newMessage.setReject();
                }
                Player toPlayer;
                if (corruptPlayerMap.containsKey(toPlayerId)) {
                    toPlayer = corruptPlayerMap.get(toPlayerId);
                } else {
                    toPlayer = honestPlayerMap.get(toPlayerId);
                }
                final Task newTask = new Task(
                        toPlayer,
                        newMessage,
                        delay
                );
                voteMessageList.add(newTask);
            }
        }
    }

    public void processBlockProposal(final int round) {
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            destPlayer.processBlockProposal(round);
        }
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            destPlayer.processBlockProposal(round);
        }
    }

    public void processVotesForEachPlayer(final int round) {
        final int totalNumPlayer = honestPlayerMap.size() + corruptPlayerMap.size();
        //the threshold guarantees at least 2/3 of the player
        final int notorizedThreshold = (int)Math.ceil((double)totalNumPlayer * 2.0 / 3.0);
        // look at each of the blocks that are accumulating votes
        // if contains at least 2/3 votes, notorize it and put it in the chain
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            destPlayer.processVotes(notorizedThreshold);
        }
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            destPlayer.processVotes(notorizedThreshold);
        }
    }

    public void finalizeChainForEachPlayer(final int round) {
        // for each of the player look at the chain head
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            destPlayer.tryFinalizeChain();
        }
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            destPlayer.tryFinalizeChain();
        }
    }

    public void endRoundForPlayers(final int round) {
        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            destPlayer.endRound();
        }
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final StreamletPlayer destPlayer = (StreamletPlayer) entry.getValue();
            destPlayer.endRound();
        }
    }

}
