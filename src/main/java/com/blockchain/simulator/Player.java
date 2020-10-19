package com.blockchain.simulator;

public abstract class Player {
    protected int id;
    protected PlayerController playerController;
    public Player(final int id, PlayerController playerController) {
        this.id = id;
        this.playerController = playerController;
    }

    public abstract void receiveMessage(final Message message, int round);

}
