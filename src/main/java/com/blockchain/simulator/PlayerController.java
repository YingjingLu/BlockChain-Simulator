package com.blockchain.simulator;
import java.util.Map;

public abstract class PlayerController {

    protected final NetworkSimulator networkSimulator;
    protected final CryptographyAuthenticator authenticator;
    protected final Map<Integer, Player> honestPlayerMap;
    protected final Map<Integer, Player> corruptPlayerMap;

    protected int curRound;

    public PlayerController(
            final NetworkSimulator networkSimulator,
            final CryptographyAuthenticator authenticator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap) {
        this.networkSimulator = networkSimulator;
        this.honestPlayerMap = honestPlayerMap;
        this.corruptPlayerMap = corruptPlayerMap;
        this.authenticator = authenticator;
        curRound = 0;
    }
}
