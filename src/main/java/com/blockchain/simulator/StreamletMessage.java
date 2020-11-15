package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.List;

public class StreamletMessage extends Message {
    // true for vote message, false for block proposal message
    final boolean isVote;
    Bit approved;
    final int proposerId;
    public StreamletMessage(
            final boolean isVote,
            final int inRound,
            final List<Bit> inMessage,
            final int inFromPlayerId,
            final int inToPlayerId,
            final int proposerId) {
        super(inRound, inMessage, inFromPlayerId, inToPlayerId);
        this.isVote = isVote;
        approved = Bit.FLOOR;
        this.proposerId = proposerId;
    }
    public static StreamletMessage CreateMessageFromBit(
            final boolean isVote,
            final int inRound,
            final Bit inMessage,
            final int inFromPlayerId,
            final int inToPlayerId,
            final int proposerId) {
        final List<Bit> message = new LinkedList<Bit>();
        message.add(inMessage);
        return new StreamletMessage(isVote, inRound, message, inFromPlayerId, inToPlayerId, proposerId);
    }

    public Message deepCopy() {
        List<Bit> newMessageList = new LinkedList<Bit>();
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
        for (final Bit b : this.getMessage()) {
            newMessage.getMessage().add(b);
        }
        return newMessage;
    }
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
}
