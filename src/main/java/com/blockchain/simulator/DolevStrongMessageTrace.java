package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;

/**
 * Dolev strong message trace object
 */
public class DolevStrongMessageTrace {
    List<Task> taskList;
    public DolevStrongMessageTrace(List<Task> messageList) {
        taskList = messageList;
    }
}
