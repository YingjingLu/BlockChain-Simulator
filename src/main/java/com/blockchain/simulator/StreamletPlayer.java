package com.blockchain.simulator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Math;

public class StreamletPlayer extends Player {
    public StreamletBlock chainHead;
    public Map<Integer, StreamletBlock> chainTailMap;
    public Map<Integer, StreamletBlock> blockMap;
    // block round id, block message
    public List<StreamletMessage> curRoundInputMessageList;
    public List<StreamletMessage> curRoundMessageList;
    public Map<Integer, StreamletMessage> blockIdToBlockMessageMap;
    public Map<Integer, Integer> blockIdToVoteCountMap;
    public List<StreamletBlock> blockPendingVotingForCurRound;
    public Set<Integer> pendingTransactionSet;
    public Set<Integer> confirmedTransactionSet;
    public Set<String> receivedMessageHashSet;
    public int longestNotarizedChainLevel;

    public StreamletPlayer(final int id, PlayerController playerController) {
        super(id, playerController);
        chainHead = StreamletBlock.getGenesisBlock();
        chainTailMap = new HashMap<>();
        blockMap = new HashMap<>();
        addTailToMap(chainHead);
        blockMap.put(chainHead.getEpoch(), chainHead);
        curRoundInputMessageList = new LinkedList<>();
        curRoundMessageList = new LinkedList<>();
        blockIdToBlockMessageMap = new HashMap<>();
        blockIdToVoteCountMap = new HashMap<>();
        blockPendingVotingForCurRound = new LinkedList<>();
        pendingTransactionSet = new HashSet<>();
        confirmedTransactionSet = new HashSet<>();
        receivedMessageHashSet = new HashSet<>();
        // starting from genesis block
        longestNotarizedChainLevel = 0;

    }
    public void receiveInput(final Message message) {
        // only accept messages not received before
        final String messageHash = ((StreamletMessage) message).getHashString();
        if (messageHashSeen(messageHash)) {
            return;
        }
        StreamletMessage streamletMessage = (StreamletMessage) message;
        curRoundInputMessageList.add(streamletMessage);
        processInputMessage(streamletMessage);
        receivedMessageHashSet.add(messageHash);
    }

    public void processInputMessage(StreamletMessage message) {
        final String messageHash = (message).getHashString();
        if (messageHashSeen(messageHash)) {
            return;
        }

        for (int tx : message.getMessage()) {
            if (!confirmedTransactionSet.contains(tx)) {
                pendingTransactionSet.add(tx);
            }
        }
        receivedMessageHashSet.add(messageHash);
    }

    /**
     * Filter from the received message list to get broadcasted input messages
     * process them and remove those messages from currentMessageList
     */
    public void proceeeInputs() {
        List<StreamletMessage> filteredMessageList = new LinkedList<>();
        for (StreamletMessage message : curRoundMessageList) {
            if (message.isInputMessage()) {
                processInputMessage(message);
            } else {
                filteredMessageList.add(message);
            }
        }
        curRoundMessageList.clear();
        curRoundMessageList = filteredMessageList;
    }

    public void receiveMessage(final Message message, final int round) {
        curRoundMessageList.add((StreamletMessage) message);
    }

    public void addTailToMap(final StreamletBlock block) {
        assert block != null : "block should not be null";
        assert ! chainTailMap.containsKey(block.getEpoch()) : "Adding a new block already exists in the chain";
        chainTailMap.put(block.getEpoch(), block);
    }

    public void processBlockProposal(final int curRound) {
        List<StreamletBlock> pendingAddedBlockList = new LinkedList<>();
        List<StreamletMessage> filteredVoteMessageList = new LinkedList<>();
        // iterate over all messages, filter those block proposals, construct them into blocks,
        // move those vote messages into the
        for (StreamletMessage streamletMessage : curRoundMessageList) {
            if (!streamletMessage.getIsVote()) {
                // construct block from message
                if (blockIdToBlockMessageMap.containsKey(streamletMessage.getEpoch())) {
                    System.out.println("WARNING: either from proposal or from echoing do I see repeating block proposal: " + streamletMessage.getEpoch());
                } else {
                    StreamletBlock block = new StreamletBlock(
                            streamletMessage.getEpoch(),
                            streamletMessage.getProposerId(),
                            new LinkedList<>(streamletMessage.getMessage())
                    );
                    pendingAddedBlockList.add(block);
                    blockIdToBlockMessageMap.put(streamletMessage.getEpoch(), streamletMessage);
                }
            } else {
                filteredVoteMessageList.add(streamletMessage);
            }
        }
        curRoundMessageList.clear();
        curRoundMessageList = filteredVoteMessageList;
        // sort the pending added blocks by round ascending and add them from lower to higher
        pendingAddedBlockList.sort(new StreamletBlockSortByEpoch());
        for (StreamletBlock block : pendingAddedBlockList) {
            final int round = block.getEpoch();
            assert chainTailMap.containsKey(round) : "Added votes should have existing prev in head";
            StreamletMessage blockMessage = blockIdToBlockMessageMap.get(round);
            List<String> signatures  = blockMessage.getSignatures();
            // get the last two signatures
            assert signatures.size() >= 2 : "Message should have at least 2 signatures";
            final int size = signatures.size();
            final StreamletMessage prevMessage = CryptographyAuthenticator.signatureToStreamletMessage(
                    signatures.get(size - 1)
            );
            final StreamletMessage curMessage = CryptographyAuthenticator.signatureToStreamletMessage(
                    signatures.get(size - 2)
            );
            final StreamletBlock prev = blockMap.get(prevMessage.getEpoch());
            // if the chain is not stemming from tail
            assert prev != null : "prev block cannot be null";
            // set prev
            block.setPrev(prev);
            // set level
            block.setLevel(prev.getLevel() + 1);

            blockMap.put(block.getEpoch(), block);
            // update head
            if (!prev.isGenesisBlock() && chainTailMap.containsKey(prev.getEpoch())) {
                chainTailMap.remove(prev.getEpoch());
            }
            chainTailMap.put(block.getEpoch(), block);
            blockIdToVoteCountMap.put(block.getEpoch(), 0);
            blockPendingVotingForCurRound.add(block);
        }
    }

    /**
     * Determine if the given block satisfies the following constraints:
     * 1. The block exists in current player's blockMap
     * 2. Its predecessor exists in the current player's view and it is notarized
     * 3. the block is extending one of the longest notarized chain
     *
     * @param proposedBlock
     * @return
     */
    public boolean determineBlockVote(final StreamletBlock proposedBlock) {
        if (!blockMap.containsKey(proposedBlock.getEpoch())) {
            return false;
        }
        final int predecessorBlockRound = proposedBlock.getPrev().getEpoch();
        if (!blockMap.containsKey(predecessorBlockRound)) {
            return false;
        }
        if (!blockMap.get(predecessorBlockRound).getNotorized()) {
            return false;
        }
        // should extend the longest level and the block extending from should be notarized
        boolean res = blockMap.get(predecessorBlockRound).getLevel() == longestNotarizedChainLevel;
        res = res && blockMap.get(predecessorBlockRound).getNotorized();
        return res;
    }

    public void processVotes(final int notorizedThreshold) {
        List<Integer> pendingNotorizedBlockList = new LinkedList<>();
        for (Message msg : curRoundMessageList) {
            if (((StreamletMessage)msg).getIsVote()) {
                StreamletMessage streamletMessage = (StreamletMessage) msg;
                // drop votes that have already been notorized or for blocks not yet received
                // drop votes for rejection also
                if (streamletMessage.getApproved()) {
                    final int blockRound = streamletMessage.getRound();
                    if (blockIdToVoteCountMap.containsKey(blockRound)) {
                        blockIdToVoteCountMap.put(blockRound, blockIdToVoteCountMap.get(blockRound) + 1);
                        // check if surpassing more than 2/3 vote
                        // if so then notorize the block, place the block into the chain
                        if (blockIdToVoteCountMap.get(blockRound) >= notorizedThreshold) {
                            // construct block from message
                            pendingNotorizedBlockList.add(blockRound);
                            // remove the block id from both maps
                            blockIdToVoteCountMap.remove(blockRound);
                        }
                    }
                }
            }
        }
        for (int blockRound : pendingNotorizedBlockList) {
            assert blockMap.containsKey(blockRound) : "Block map should contain blocks for received votes";
            blockMap.get(blockRound).setNotorized();
            longestNotarizedChainLevel = Math.max(longestNotarizedChainLevel, blockMap.get(blockRound).getLevel());
        }
        curRoundMessageList.clear();
    }

    public void tryFinalizeChain() {
        // iterate through each of tail heads in the map
        for (Map.Entry<Integer, StreamletBlock> entry : chainTailMap.entrySet()) {
            StreamletBlock tailBlock = entry.getValue();
            while(tailBlock != null && !tailBlock.getNotorized()) {
                tailBlock = tailBlock.getPrev();
            }
            if (tailBlock == null) {
                 continue;
            }
            List<StreamletBlock> blockList = new ArrayList<>();
            // try accumulate three blocks
            StreamletBlock cur = tailBlock;
            blockList.add(cur);
            if (cur.getPrev() != null && cur.getPrev().getNotorized()) {
                cur = cur.getPrev();
                blockList.add(cur);
            } else {
                continue;
            }
            if (cur.getPrev() != null && cur.getPrev().getNotorized()) {
                cur = cur.getPrev();
                blockList.add(cur);
            } else {
                continue;
            }
            // if the three blocks have consecutive rounds
            if (blockList.get(0).getEpoch() - blockList.get(1).getEpoch() == 1
                && blockList.get(1).getEpoch() - blockList.get(2).getEpoch() == 1
                && !blockList.get(0).isGenesisBlock()
                && !blockList.get(2).isGenesisBlock()) {
                // finalize the first two
                cur = blockList.get(1);
                while(cur != null && !cur.getFinalized()) {
                    assert cur.getNotorized() : "the finalized chain should only contains notarized blocks";
                    cur.setFinalized();
                    for (int tx : cur.getMessage()) {
                        pendingTransactionSet.remove(tx);
                        confirmedTransactionSet.add(tx);
                    }
                    cur = cur.getPrev();
                }
            }
        }
    }

    public void endRound() {
        // clear out the blocks this player has voted for in current round
        blockPendingVotingForCurRound.clear();
        curRoundMessageList.clear();
        curRoundInputMessageList.clear();
    }

    public boolean messageHashSeen(final String messageHash) {
        return this.receivedMessageHashSet.contains(messageHash);
    }
}
