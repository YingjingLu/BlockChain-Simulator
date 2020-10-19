package com.blockchain.simulator;

import java.util.Map;
import java.util.HashMap;

public class RoundSimulator {

    // total number of rounds to simulate
    // -1 if runs until terminating condition
    public final int totalRounds;
    public final Map<Integer, ByzantinePlayer> honestPlayerMap;
    public final Map<Integer, ByzantinePlayer> corruptPlayerMap;

    public RoundSimulator(final int totalRounds, final int totalPlayer, final int corruptPlayer) {
        this.totalRounds = totalRounds;
        honestPlayerMap = new HashMap<>();
        corruptPlayerMap = new HashMap<>();

        final int startCorrupt = totalPlayer - corruptPlayer;
        for (int i = 0; i < totalPlayer; i++) {
            if (i >= startCorrupt) {
                corruptPlayerMap.put(i, new ByzantinePlayer(i));
            } else {
                honestPlayerMap.put(i, new ByzantinePlayer(i));
            }
        }
    }

    /**
     * Run the Dolev Strong protocol communication
     * @return
     */
    public int runByzantineBroadcast() {
        // round 0: give sender an input bit
        int inputBit = 1;
        int sender = 0;

        // send initial bit to the sender through the network




        return 0;
    }
}
