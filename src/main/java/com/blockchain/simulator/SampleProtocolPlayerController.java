package com.blockchain.simulator;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;
/**
 * The sample player controller for the sample protocol.
 * Responsible for processing messages send those messages to the network simulator to be broadcaseed to the players
 *
 * This module have access to the network simulator, crypto authenticator and honest, corrupt player
 * THe adversary and honest strategies modules are employed mainly in this module
 */
public class SampleProtocolPlayerController extends PlayerController {
    public SampleProtocolPlayerController(
            final NetworkSimulator networkSimulator,
            final CryptographyAuthenticator authenticator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap,
            final Map<Integer, Player> playerMap) {
        super(networkSimulator, authenticator, honestPlayerMap, corruptPlayerMap, playerMap);
    }

    /**
     * Notify each player that a round has begun, initialize round specific state
     * @param round
     */
    public void beginRound(final int round) {
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final SampleProtocolPlayer player = (SampleProtocolPlayer) entry.getValue();
            player.beginRound(round);
        }

        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final SampleProtocolPlayer player = (SampleProtocolPlayer) entry.getValue();
            player.beginRound(round);
        }
    }

    /**
     * @custom.adversary_dependent: Process the input for each player for a given rounds
     * Convert those messages to be tasks to send to the network
     * Adversary can control the delays and whether honest players can receive input at all
     *
     * @param messageList
     * @param round
     * @return The list of network patches with their destination player and network delay
     */
    public List<Task> receiveInputForRound(final List<SampleProtocolMessage> messageList, final int round) {
        // TODO: implement a strategy to process input to be sent to every player for a round
        return null;
    }

    /**
     * @custom.adversary_dependent: Create the messages to be sent to other players
     * @custom.protocol_dependent: Depend on how protocol defines how each honest players should send messages
     * This involves how honest players send messages to other players, but also how corrupt players send what messages
     * to other players.
     *
     * Both honest players' messages and corrupt players' messages can be processed by the Adversary strategy and
     * converted into Tasks in the end to be send to network
     *
     * @param round
     * @return
     */
    public List<Task> generateMessageTasksAmongPlayers(final int round) {
        // TODO: implement the strategy to create the message to to send to other players for each player
        return null;
    }

    /**
     * Notify every player that a round has ended to clear out state specific to that state
     *
     * @param round
     */
    public void endRoundForPlayers(final int round) {
        for (Map.Entry<Integer, Player> entry : corruptPlayerMap.entrySet()) {
            final SampleProtocolPlayer player = (SampleProtocolPlayer) entry.getValue();
            player.endRound();
        }

        for (Map.Entry<Integer, Player> entry : honestPlayerMap.entrySet()) {
            final SampleProtocolPlayer player = (SampleProtocolPlayer) entry.getValue();
            player.endRound();
        }
    }

    /**
     * Create the output for each player indexed by their player id
     * @return The list of outputs for each player for a given round. Indexed by their id
     */
    public List<SampleProtocolMessage> createOutputForEveryPlayer() {
        final int totalPlayer = honestPlayerMap.size() + corruptPlayerMap.size();
        final List<SampleProtocolMessage> resList = new LinkedList<>();
        for (int i = 0; i < totalPlayer; i ++) {
            if (honestPlayerMap.containsKey(i)) {
                resList.add(((SampleProtocolPlayer) honestPlayerMap.get(i)).getOutput());
            } else {
                resList.add(((SampleProtocolPlayer) corruptPlayerMap.get(i)).getOutput());
            }
        }
        return resList;
    }

    /**
     * TODO: implement the way to print all players' status in stdout
     *
     */
    public void printOutput() {

    }
}
