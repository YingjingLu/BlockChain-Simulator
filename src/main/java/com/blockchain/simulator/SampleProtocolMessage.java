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

    List<Integer> message;
    public static final String splitter = "&";

    public SampleProtocolMessage(final int inRound, final List<Integer> inMessage, final int inFromPlayerId, final int inToPlayerId) {
        super(inRound, inFromPlayerId, inToPlayerId);
        this.message = inMessage;
    }

    public List<Integer> getMessage() {
        return message;
    }

    public String messageToString() {
        StringBuilder builder = new StringBuilder();
        for (int i : message) {
            builder.append(i);
            builder.append(splitter);
        }
        return builder.toString();
    }

    public static List<Integer> stringToMessage(String str) {
        String[] splitArray = str.split(splitter, 0);
        List<Integer> res = new LinkedList<>();
        for (String s : splitArray) {
            res.add(Integer.parseInt(s));
        }
        return res;
    }

    public Message deepCopy() {
        List<Integer> newMessageList = new LinkedList<Integer>();
        SampleProtocolMessage newMessage = new SampleProtocolMessage(round, newMessageList, fromPlayerId, toPlayerId);
        // copy signature
        for (final String sign : this.getSignatures()) {
            newMessage.getSignatures().add(sign);
        }
        // copy message
        for (final Integer b : this.getMessage()) {
            newMessage.getMessage().add(b);
        }
        return newMessage;
    }
}
