package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.List;
import java.lang.StringBuilder;

public abstract class Message {
    protected List<String> signatures;
    int round;
    protected int fromPlayerId;
    protected int toPlayerId;

    public Message(final int inRound, final int inFromPlayerId, final int inToPlayerId) {
        round = inRound;
        fromPlayerId = inFromPlayerId;
        toPlayerId = inToPlayerId;
        signatures = new LinkedList<>();
    }

    public abstract Message deepCopy();

    public abstract String messageToString();

    public void addSignature(final String signature) {
        this.signatures.add(signature);
    }

    public void addSignatures(final List<String> signature) {
        this.signatures.addAll(signature);
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

    public List<String> getSignatures() {
        return signatures;
    }
}
