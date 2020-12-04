package com.blockchain.simulator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.lang.Math;

public class NetworkSimulator {

    public static final int INFINITE_ROUND = -1;
    public static final int PARTIALLY_SYNC_DELTA = -1;
    private int curRound;

    private final Map<Integer, Queue<Task>> messageQueue;

    public NetworkSimulator() {
        curRound = 0;
        messageQueue = new HashMap<>();
    }

    public void beginRound(final int round) {
        curRound = round;
    }

    /**
     * Update the current round, set the local variable
     * If there are messages scheduled for current round, then pop all of them and send them to the player
     * @param round
     */
    public void sendMessagesToPlayers(int round) {
        curRound = round;
        if (messageQueue.containsKey(curRound)) {
            final Queue<Task> q = messageQueue.remove(curRound);
            while (q.size() > 0) {
                final Task task = q.remove();
                task.getTargetPlayer().receiveMessage(task.getMessage(), curRound);
            }
        }
    }

    public void boundMessageDelayForSynchronousNetwork(final int delta, List<Task> taskList) {
        if (delta != PARTIALLY_SYNC_DELTA) {
            for (Task t : taskList) {
                if (t.getDelay() == INFINITE_ROUND) {
                    t.setDelay(delta);
                } else {
                    t.setDelay(Integer.min(delta, t.getDelay()));
                }
            }
        }
    }

    public void addTaskToNetworkQueue(final int curRound, final Task task) {
        if (task.getDelay() == INFINITE_ROUND) {
            return;
        } else if (task.getDelay() < 0) {
            System.out.println("Error: delay should be non-negative, given " + task.getDelay());
            return;
        }
        final int targetRound = curRound + task.getDelay();
        if (!messageQueue.containsKey(targetRound)) {
            messageQueue.put(targetRound, new LinkedList<Task>());
        }
        messageQueue.get(targetRound).add(task);
    }

}
