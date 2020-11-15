package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;

public class NetworkSimulator {

    public static final int INFINITE_ROUND = -1;

    private int curRound;

    private final Map<Integer, Queue<Task>> messageQueue;

    public NetworkSimulator() {
        curRound = 0;
        messageQueue = new HashMap<>();
    }

    /**
     * Update the current round, set the local variable
     * If there are messages scheduled for current round, then pop all of them and send them to the player
     * @param round
     */
    public void beginRound(int round) {
        curRound = round;
        if (messageQueue.containsKey(curRound)) {
            final Queue<Task> q = messageQueue.remove(curRound);
            while (q.size() > 0) {
                final Task task = q.remove();
                task.getTargetPlayer().receiveMessage(task.getMessage(), curRound);
            }
        }
    }

    /**
     * Send a message with the number of round of delay
     *
     * @param message the message pre-configured with destination player
     * @param delay the number of rounds that delays the message
     */
    public void sendMessage(Player player, final Message message, final int delay) {
        Task task = new Task(player, message, delay);
        addTaskToNetworkQueue(curRound, task);
        System.out.println("Round " + message.getRound() + "From Player " + message.getFromPlayerId() + " To Player " + message.getToPlayerId() + "Bit" + message.getMessage().get(0).toString() );
    }

    public void addTaskToNetworkQueue(final int curRound, final Task task) {
        if (task.getDelay() == INFINITE_ROUND) {
            return;
        }
        final int targetRound = curRound + task.getDelay();
        if (!messageQueue.containsKey(targetRound)) {
            messageQueue.put(targetRound, new LinkedList<Task>());
        }
        messageQueue.get(targetRound).add(task);
    }

}
