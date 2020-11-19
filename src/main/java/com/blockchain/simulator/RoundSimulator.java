package com.blockchain.simulator;

import java.util.Map;
import java.util.HashMap;

public abstract class RoundSimulator {


    public final Map<Integer, Player> honestPlayerMap;
    public final Map<Integer, Player> corruptPlayerMap;
    public final NetworkSimulator networkSimulator;

    public final CryptographyAuthenticator authenticator;

    public RoundSimulator() {
        honestPlayerMap = new HashMap<>();
        corruptPlayerMap = new HashMap<>();
        networkSimulator = new NetworkSimulator();
        authenticator = new CryptographyAuthenticator();
    }

    public boolean isPlayerHonest(final int playerId) {
        return honestPlayerMap.containsKey(playerId);
    }
}
