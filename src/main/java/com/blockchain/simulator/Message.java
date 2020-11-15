package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.List;
import java.lang.StringBuilder;

public abstract class Message {
    protected List<String> signatures;
    int round;
    protected List<Bit> message;
    protected int fromPlayerId;
    protected int toPlayerId;

    public Message(final int inRound, final List<Bit> inMessage, final int inFromPlayerId, final int inToPlayerId) {
        round = inRound;
        message = inMessage;
        fromPlayerId = inFromPlayerId;
        toPlayerId = inToPlayerId;
        signatures = new LinkedList<>();
    }

    public abstract Message deepCopy();

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

    public static List<Bit> stringToBitMessage(String str) {
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

    public void addSignature(final String signature) {
        this.signatures.add(signature);
    }



    public void setRound(final int round) {
        this.round = round;
    }

    public void setFromPlayerId(final int id) {
        this.fromPlayerId = id;
    }

    public void setToPlayerId(final int id) {
        this.toPlayerId = id;
    }

    public int getRound() {
        return this.round;
    }

    public int getFromPlayerId() {
        return this.fromPlayerId;
    }

    public int getToPlayerId() {
        return this.toPlayerId;
    }

    public List<Bit> getMessage() {
        return this.message;
    }

    public List<String> getSignatures() {
        return signatures;
    }
}
