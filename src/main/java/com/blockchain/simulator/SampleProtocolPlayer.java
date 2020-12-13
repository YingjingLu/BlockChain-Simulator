package com.blockchain.simulator;

public class SampleProtocolPlayer extends Player {

    public SampleProtocolPlayer(final int id, PlayerController playerController) {
        super(id, playerController);
    }

    public void receiveMessage(final Message message, int round) {
    }

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
     * Clear out the sround specific state for that player
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
