package com.blockchain.simulator;

import java.util.Map;
import java.util.HashMap;

public abstract class RoundSimulator {


    public final Map<Integer, Player> honestPlayerMap= new HashMap<>();
    public final Map<Integer, Player> corruptPlayerMap = new HashMap<>();
    public final NetworkSimulator networkSimulator = new NetworkSimulator();

    public final CryptographyAuthenticator authenticator = new CryptographyAuthenticator();

    public abstract int run();

    public boolean isPlayerHonest(final int playerId) {
        return honestPlayerMap.containsKey(playerId);
    }
}
