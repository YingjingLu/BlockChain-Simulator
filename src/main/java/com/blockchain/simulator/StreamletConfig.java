package com.blockchain.simulator;

public class StreamletConfig {
    public final int round;
    public final int numTotalPlayer;
    public final int numCorruptPlayer;
    public final boolean useTrace;

    public StreamletConfig(final int round, final int numTotalPlayer, final int numCorruptPlayer, final boolean useTrace) {
        this.round = round;
        this.numTotalPlayer = numTotalPlayer;
        this.numCorruptPlayer = numCorruptPlayer;
        this.useTrace = useTrace;
    }
}
