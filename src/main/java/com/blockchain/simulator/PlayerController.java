package com.blockchain.simulator;
import java.util.Map;

public abstract class PlayerController {

    protected final NetworkSimulator networkSimulator;
    protected final Map<Integer, Player> honestPlayerMap;
    protected final Map<Integer, Player> corruptPlayerMap;

    protected int curRound;

    public PlayerController(final NetworkSimulator networkSimulator, final Map<Integer, Player> honestPlayerMap, final Map<Integer, Player> corruptPlayerMap) {
        this.networkSimulator = networkSimulator;
        this.honestPlayerMap = honestPlayerMap;
        this.corruptPlayerMap = corruptPlayerMap;
        curRound = 0;
    }


    public void updateRound(final int round) {
        curRound = round;
    }

    public abstract void sendMessagesToOtherPlayersViaNetwork(final int round);

    public abstract void endRoundForPlayers(final int round);
}
