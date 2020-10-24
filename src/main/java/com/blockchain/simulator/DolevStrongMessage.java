package com.blockchain.simulator;

import java.util.List;
import java.util.LinkedList;

public class DolevStrongMessage extends Message {

    public DolevStrongMessage(final int inRound, final List<Bit> inMessage, final int inFromPlayerId, final int inToPlayerId) {
        super(inRound, inMessage, inFromPlayerId, inToPlayerId);
    }

    public static DolevStrongMessage CreateMessageFromBit(
            final int inRound,
            final Bit inMessage,
            final int inFromPlayerId,
            final int inToPlayerId) {
        final List<Bit> message = new LinkedList<Bit>();
        message.add(inMessage);
        return new DolevStrongMessage(inRound, message, inFromPlayerId, inToPlayerId);
    }

    public Message deepCopy() {
        List<Bit> newMessageList = new LinkedList<Bit>();
        DolevStrongMessage newMessage = new DolevStrongMessage(round, newMessageList, fromPlayerId, toPlayerId);
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
}
