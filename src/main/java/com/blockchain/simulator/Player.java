package com.blockchain.simulator;

public abstract class Player {
    protected int id;
    protected PlayerController playerController;
    public Player(final int id, PlayerController playerController) {
        this.id = id;
        this.playerController = playerController;
    }
    public abstract void receiveInput(final Message message);
    public abstract void receiveMessage(final Message message, int round);
    public abstract void endRound();
    public int getId() {
        return this.id;
    }

}
