package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;

/**
 * Dolev Strong message trace object
 */
public class DolevStrongMessageTrace {
    List<Task> taskList;
    public DolevStrongMessageTrace(List<Task> messageList) {
        taskList = messageList;
    }
}
