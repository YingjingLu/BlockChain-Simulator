package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.lang.StringBuilder;

public class StreamletMessage extends Message {
    // true for vote message, false for block proposal message
    public static final String splitter = "&";
    public static final String HASH_STRING_SPLITTER = "|";
    public static final String EMPTY_MESSAGE = "EMPTY_MESSAGE";
    final boolean isVote;
    Bit approved;
    final int proposerId;
    public List<Integer> message;
    public StreamletMessage(
            final boolean isVote,
            final int inRound,
            final List<Integer> inMessage,
            final int inFromPlayerId,
            final int inToPlayerId,
            final int proposerId) {
        super(inRound, inFromPlayerId, inToPlayerId);
        this.isVote = isVote;
        approved = Bit.FLOOR;
        this.proposerId = proposerId;
        this.message = inMessage;
    }

    public String messageToString() {
        if (message.size() == 0) {
            return EMPTY_MESSAGE;
        }
        StringBuilder builder = new StringBuilder();
        Iterator<Integer> messageIterator = message.listIterator();
        while (messageIterator.hasNext()) {
            builder.append(messageIterator.next());
            if (messageIterator.hasNext()) {
                builder.append(splitter);
            }
        }
        return builder.toString();
    }

    public static List<Integer> stringToMessage(String str) {
        List<Integer> res = new LinkedList<>();
        if (str.equals(EMPTY_MESSAGE)) {
            return res;
        }
        String[] splitArray = str.split(splitter, 0);
        for (String s : splitArray) {
            res.add(Integer.parseInt(s));
        }
        return res;
    }

    public Message deepCopy() {
        List<Integer> newMessageList = new LinkedList<>();
        StreamletMessage newMessage = new StreamletMessage(
                isVote,
                round,
                newMessageList,
                fromPlayerId,
                toPlayerId,
                proposerId
        );
        newMessage.approved = this.approved;
        // copy signature
        for (final String sign : this.getSignatures()) {
            newMessage.getSignatures().add(sign);
        }
        // copy message
        for (int b : this.getMessage()) {
            newMessage.getMessage().add(b);
        }
        return newMessage;
    }

    /**
     * Get a unique hash string of this message for players to identify if this message has been received before
     *
     * For input txs, every player should only receive once,
     * input messages with same set of txs should be treated as the same message
     * Thus hash with isVote, epoch, message should be fine
     *
     * for proposal, every player should receive block once, does not matter who sends the proposal.
     * Thus proposal with the same block and proposerId should treated as the same message
     * Thus, hash with isVote, epoch should be fine
     *
     * For votes, every player needs to know the block, whose vote, and it is a vote should be fine
     * Thus vote message with same fromPlayerId. block should be treated as the same message
     * Thus hash with isVote, epoch, fromPlayerId should be fine
     * @return THe hash string of the message per schema above
     */
    public String getHashString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (isVote) {
            stringBuilder.append(1);
        } else {
            stringBuilder.append(0);
        }
        stringBuilder.append(HASH_STRING_SPLITTER);
        stringBuilder.append(getEpoch());

        if (isInputMessage()) {
            stringBuilder.append(HASH_STRING_SPLITTER);
            stringBuilder.append(messageToString());
        } else {
            if (isVote) {
                stringBuilder.append(HASH_STRING_SPLITTER);
                stringBuilder.append(getFromPlayerId());
            }
        }
        return stringBuilder.toString();
    }

    public boolean isInputMessage() {
        return getRound() == Globals.streamletInputMessageRound;
    }

    // in Streamlet the round attribute is overloaded into the epoch number or block ID this message is for
    public int getEpoch() { return round; }
    public boolean getIsVote() {
        return isVote;
    }
    public boolean getApproved() {
        return approved == Bit.ONE;
    }
    public void setApprove() {
        approved = Bit.ONE;
    }
    public void setReject() { approved = Bit.ZERO;}
    public int getProposerId() {
        return proposerId;
    }
    public List<Integer> getMessage() { return message; }
}
