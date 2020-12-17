package com.blockchain.simulator;

/**
 * Player parent class
 */
public abstract class Player {
    protected int id;
    protected PlayerController playerController;
    public Player(final int id, PlayerController playerController) {
        this.id = id;
        this.playerController = playerController;
    }

    /**
     * Abstract method to receive input
     * @param message
     */
    public abstract void receiveInput(final Message message);

    /**
     * Abstract method to receive message
     * @param message
     * @param round
     */
    public abstract void receiveMessage(final Message message, int round);

    /**
     * Abstract method to end a round and clear round state
     */
    public abstract void endRound();

    /**
     * id getter
     * @return
     */
    public int getId() {
        return this.id;
    }

}
