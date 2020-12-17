package com.blockchain.simulator;

/**
 * Sample protocol's player object
 */
public class SampleProtocolPlayer extends Player {

    /**
     * Constructor
     * @param id
     * @param playerController
     */
    public SampleProtocolPlayer(final int id, PlayerController playerController) {
        super(id, playerController);
    }

    /**
     * receive messages of current round
     * @param message
     * @param round
     */
    public void receiveMessage(final Message message, int round) {
    }

    /**
     * receive current round input
     * @param message
     */
    public void receiveInput(final Message message) {
    }

    /**
     * Initialize the round specific state for a player
     *
     * @param round
     */
    public void beginRound(final int round) {
    }

    /**
     * Clear out the round specific state for that player
     * @param round
     */
    public void endRound() {

    }

    /**
     * @custom.protocol_dependent: The protocol's definition of ways for a player to arrive at a output given the player's current states
     *
     * @return a message containing the output
     */
    public SampleProtocolMessage getOutput() {
        return null;
    }

}
