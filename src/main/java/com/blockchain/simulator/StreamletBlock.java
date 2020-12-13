package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.List;

public class StreamletBlock {
    public final int epoch;
    public final int proposerId;
    public final List<Integer> message;
    public StreamletBlock prev;
    public boolean notorized;
    public boolean finalized;
    public int level;
    public static final int genesisTransaction = -1;

    public StreamletBlock(final int epoch, final int proposerId, final List<Integer> msg) {
        this.epoch = epoch;
        this.proposerId = proposerId;
        this.message = msg;
        prev = null;
        notorized = false;
        finalized = false;
        level = 0;
    }

    public StreamletBlock(
            final int epoch,
            final int proposerId,
            final List<Integer> msg,
            final StreamletBlock prev,
            final int level) {
        this.epoch = epoch;
        this.proposerId = proposerId;
        this.message = msg;
        this.prev = prev;
        notorized = false;
        finalized = false;
        this.level = level;
    }

    public static StreamletBlock getGenesisBlock() {
        final List<Integer> genesisMessage = new LinkedList<>();
        genesisMessage.add(genesisTransaction);
        StreamletBlock genesisBLock = new StreamletBlock(-1, -1, genesisMessage, null, 0);
        genesisBLock.setNotorized();
        return genesisBLock;
    }

    public StreamletBlock getGenesisBlockFromTailBlock() {
        if (isGenesisBlock()) {
            return this;
        }
        StreamletBlock cur = this;
        while(!cur.isGenesisBlock()) {
            cur = cur.getPrev();
        }
        return cur;
    }

    public boolean isGenesisBlock() {
        return getPrev() == null && getEpoch() == -1 && getLevel() == 0 && getProposerId() == -1;
    }

    public StreamletBlock deepCopy() {
        final List<Integer> newMessage = new LinkedList<>(getMessage());
        final StreamletBlock newBlock = new StreamletBlock(
                getEpoch(),
                getProposerId(),
                newMessage,
                getPrev(),
                getLevel()
        );
        if (getNotorized()) {
            newBlock.setNotorized();
        }
        if (getFinalized()) {
            newBlock.setFinalized();
        }
        return newBlock;
    }

    public boolean equalsExceptPrev(final StreamletBlock other) {
        return (getEpoch() == other.getEpoch()
                && getProposerId() == other.getProposerId()
                && getMessage() == other.getMessage()
                && getNotorized() == other.getNotorized()
                && getFinalized() == other.getFinalized()
                );
    }

    public int getEpoch() {
        return epoch;
    }
    public List<Integer> getMessage() {
        return message;
    }
    public int getProposerId () { return proposerId; }
    public boolean getNotorized() {
        return notorized;
    }
    public boolean getFinalized() {
        return finalized;
    }
    public int getLevel() { return level; }


    public void setPrev(StreamletBlock prev) {
        this.prev = prev;
    }
    public void setNotorized() {
        this.notorized = true;
    }
    public void setFinalized() {
        this.finalized = true;
    }
    public void setLevel(final int level) {
        this.level = level;
    }
    public StreamletBlock getPrev() {
        return prev;
    }
}
