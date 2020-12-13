package com.blockchain.simulator;

import java.util.List;
import java.util.LinkedList;

public class DolevStrongMessage extends Message {
    public List<Bit> message;
    public DolevStrongMessage(final int inRound, final List<Bit> inMessage, final int inFromPlayerId, final int inToPlayerId) {
        super(inRound, inFromPlayerId, inToPlayerId);
        message = inMessage;
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

    public String messageToString() {
        StringBuilder builder = new StringBuilder();
        for (Bit i : message) {
            switch (i) {
                case FLOOR:
                    builder.append("2");
                    break;
                case ZERO:
                    builder.append("0");
                    break;
                case ONE:
                    builder.append("1");
                    break;
                default:
                    System.out.println("Error: not a valid bit type");
                    break;
            }
        }
        return builder.toString();
    }

    public List<Bit> getMessage() {
        return this.message;
    }

    public static List<Bit> stringToMessage(String str) {
        List<Bit> res = new LinkedList<Bit>();
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '2') {
                res.add(Bit.FLOOR);
            } else if (str.charAt(i) == '1') {
                res.add(Bit.ONE);
            } else if (str.charAt((i)) == '0') {
                res.add(Bit.ZERO);
            } else {
                assert false : "Given string should be valid bit values";
            }
        }
        return res;
    }
}
