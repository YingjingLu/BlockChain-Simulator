package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.List;

/**
 * Message object that carries the protocol communication information. This message should be able to hold
 * input, inter-player communication and output information
 *
 * @custom.protocol_dependent: message data structure that holds input, out, communication information
 * TODO: configure the message in the data structure so that it holds info according to the protocol
 *
 */
public class SampleProtocolMessage extends Message {
    public SampleProtocolMessage(final int inRound, final List<Bit> inMessage, final int inFromPlayerId, final int inToPlayerId) {
        super(inRound, inMessage, inFromPlayerId, inToPlayerId);
    }

    public Message deepCopy() {
        List<Bit> newMessageList = new LinkedList<Bit>();
        SampleProtocolMessage newMessage = new SampleProtocolMessage(round, newMessageList, fromPlayerId, toPlayerId);
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
