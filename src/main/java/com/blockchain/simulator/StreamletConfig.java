package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;

public class StreamletConfig {
    public final int round;
    public final int numTotalPlayer;
    public final int numCorruptPlayer;
    public final boolean useTrace;
    public final int maxDelay;
    public final List<List<StreamletMessage>> inputMessageList;

    public StreamletConfig(
            final int round,
            final int numTotalPlayer,
            final int numCorruptPlayer,
            final boolean useTrace,
            final int maxDelay,
            List<List<StreamletMessage>> inputMessageList

    ) {
        this.round = round;
        this.numTotalPlayer = numTotalPlayer;
        this.numCorruptPlayer = numCorruptPlayer;
        this.useTrace = useTrace;
        this.maxDelay = maxDelay;
        this.inputMessageList = inputMessageList;
    }
}
