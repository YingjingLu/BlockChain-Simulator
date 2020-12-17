package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.List;
import java.lang.StringBuilder;

/**
 * Parent Message class
 */
public abstract class Message {
    protected List<String> signatures;
    int round;
    protected int fromPlayerId;
    protected int toPlayerId;

    /**
     * Constructor
     * @param inRound
     * @param inFromPlayerId
     * @param inToPlayerId
     */
    public Message(final int inRound, final int inFromPlayerId, final int inToPlayerId) {
        round = inRound;
        fromPlayerId = inFromPlayerId;
        toPlayerId = inToPlayerId;
        signatures = new LinkedList<>();
    }

    /**
     * Deep copy abstract method
     * @return
     */
    public abstract Message deepCopy();

    /**
     * message list to string abstract method
     * @return
     */
    public abstract String messageToString();

    public void addSignature(final String signature) {
        this.signatures.add(signature);
    }

    /**
     * Append signature
     * @param signature
     */
    public void addSignatures(final List<String> signature) {
        this.signatures.addAll(signature);
    }

    /**
     * Round setter
     * @param round
     */
    public void setRound(final int round) {
        this.round = round;
    }

    /**
     * fromPlayerId setter
     * @param id
     */
    public void setFromPlayerId(final int id) {
        this.fromPlayerId = id;
    }

    /**
     * toPlayerId setter
     * @param id
     */
    public void setToPlayerId(final int id) {
        this.toPlayerId = id;
    }

    /**
     * round getter
     * @return
     */
    public int getRound() {
        return this.round;
    }

    /**
     * fromPlayerId getter
     * @return
     */
    public int getFromPlayerId() {
        return this.fromPlayerId;
    }

    /**
     * toPlayerId getter
     * @return
     */
    public int getToPlayerId() {
        return this.toPlayerId;
    }

    /**
     * signature getter
     * @return
     */
    public List<String> getSignatures() {
        return signatures;
    }
}
