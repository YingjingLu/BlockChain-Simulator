package com.blockchain.simulator;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Round simulator parent class that is responsible for executing procedures
 */
public abstract class RoundSimulator {


    public final Map<Integer, Player> honestPlayerMap;
    public final Map<Integer, Player> corruptPlayerMap;
    public final Map<Integer, Player> playerMap;
    public final NetworkSimulator networkSimulator;
    public final CryptographyAuthenticator authenticator;

    /**
     * Constructor
     */
    public RoundSimulator() {
        honestPlayerMap = new HashMap<>();
        corruptPlayerMap = new HashMap<>();
        playerMap = new HashMap<>();
        networkSimulator = new NetworkSimulator();
        authenticator = new CryptographyAuthenticator();
    }
}
