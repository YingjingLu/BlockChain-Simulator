package com.blockchain.simulator;
import java.util.Random;

public class DolevStrongConfig {
    public final int round;
    public final int numCorruptPlayer;
    public final int numTotalPlayer;
    public final int maxDelay;
    final boolean useTrace;
    public int senderId;
    public Bit inputBit;

    public DolevStrongConfig(
            final int round,
            final int numCorruptPlayer,
            final int numTotalPlayer,
            final int sender,
            final int initialBit,
            final int maxDelay,
            final boolean useTrace) {
        this.round = round;
        this.numCorruptPlayer = numCorruptPlayer;
        this.numTotalPlayer = numTotalPlayer;
        this.maxDelay = maxDelay;
        this.useTrace = useTrace;
        concludeSenderId(sender);
        concludeInitialBit(initialBit);
    }


    public void concludeSenderId(final int id) {
        if (id == -1) {
            this.senderId = new Random().nextInt(numTotalPlayer);
        }
        else {
            assert id > 0 && id < numTotalPlayer : "sender id must be either -1 for random or within range 0...total player";
            this.senderId = id;
        }
    }

    public void concludeInitialBit(final int initialBit) {
        if (initialBit == -1) {
            final int random = new Random().nextInt(2);
            if (random == 0) {
                inputBit = Bit.ZERO;
            } else {
                inputBit = Bit.ONE;
            }
        } else {
            assert initialBit == 0 || initialBit == 1 : "Either bit is -1 for random or must be either 0 or 1";
            if (initialBit == 0) {
                inputBit = Bit.ZERO;
            } else {
                inputBit = Bit.ONE;
            }
        }
    }

}
