package com.blockchain.simulator;

public class Task {
    private final Player targetPlayer;
    private final Message message;

    public Task(final Player targetPlayer, final Message message) {
        this.targetPlayer = targetPlayer;
        this.message = message;
    }

    public Player getTargetPlayer() {
        return this.targetPlayer;
    }

    public Message getMessage() {
        return message;
    }
}
