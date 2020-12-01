package com.blockchain.simulator;

/**
 * Configuration object that holds the protocol's initial configuration setup information
 *
 * @custom.protocol_dependent: Configuration depending on the protocol
 *
 * TODO: define the JSON compatible data structure so that it can hold configuration of protocol from config.json
 */
public class SampleProtocolConfig {
    public final int round;
    public final int numCorruptPlayer;
    public final int numTotalPlayer;
    public final int maxDelay;
    final boolean useTrace;

    public SampleProtocolConfig (
            final int round,
            final int numCorruptPlayer,
            final int numTotalPlayer,
            final int maxDelay,
            final boolean useTrace) {
        this.round = round;
        this.numCorruptPlayer = numCorruptPlayer;
        this.numTotalPlayer = numTotalPlayer;
        this.maxDelay = maxDelay;
        this.useTrace = useTrace;
    }
}
