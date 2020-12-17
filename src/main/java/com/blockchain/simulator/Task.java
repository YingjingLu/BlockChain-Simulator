package com.blockchain.simulator;

/**
 * Network package task object
 */
public class Task {
    private final Player targetPlayer;
    private final Message message;
    private int delay;

    /**
     * Constructor
     * @param targetPlayer
     * @param message
     * @param delay
     */
    public Task(final Player targetPlayer, final Message message, final int delay) {
        this.targetPlayer = targetPlayer;
        this.message = message;
        this.delay = delay;
    }

    public Player getTargetPlayer() {
        return this.targetPlayer;
    }

    public Message getMessage() {
        return message;
    }
    public int getDelay() {
        return delay;
    }

    public void setDelay(final int delay) {
        this.delay = delay;
    }
}
