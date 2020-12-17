package com.blockchain.simulator;
import java.util.List;
import java.util.Map;

/**
 * PlayerController responsible for communicating with players to generate messages
 */
public abstract class PlayerController {

    protected final NetworkSimulator networkSimulator;
    protected final CryptographyAuthenticator authenticator;
    protected final Map<Integer, Player> honestPlayerMap;
    protected final Map<Integer, Player> corruptPlayerMap;
    protected final Map<Integer, Player> playerMap;

    protected int curRound;

    /**
     * Constructor
     * @param networkSimulator
     * @param authenticator
     * @param honestPlayerMap
     * @param corruptPlayerMap
     * @param playerMap
     */
    public PlayerController(
            final NetworkSimulator networkSimulator,
            final CryptographyAuthenticator authenticator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap,
            final Map<Integer, Player> playerMap) {
        this.networkSimulator = networkSimulator;
        this.honestPlayerMap = honestPlayerMap;
        this.corruptPlayerMap = corruptPlayerMap;
        this.authenticator = authenticator;
        this.playerMap = playerMap;
        curRound = 0;
    }

    /**
     * Iterate through all tasks add then to the network message queue.
     * Nothing fancy here
     *
     * @param curRound
     * @param messageTaskList
     */
    public void sendMessageListViaNetwork(final int curRound, final List<Task> messageTaskList) {
        for (final Task task : messageTaskList) {
            networkSimulator.addTaskToNetworkQueue(curRound, task);
        }
    }
}
