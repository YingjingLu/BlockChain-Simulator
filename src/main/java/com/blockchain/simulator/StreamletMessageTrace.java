package com.blockchain.simulator;
import java.util.List;

/**
 * Streamlet message trace object
 */
public class StreamletMessageTrace {
    public final List<Task> proposalMessage;
    public final List<Task> voteMessage;
    public final List<Task> transactionEcho;
    public final List<Task> messageEcho;

    /**
     * Constructor
     * @param proposalMessage
     * @param voteMessage
     * @param transactionEcho
     * @param messageEcho
     */
    public StreamletMessageTrace (
            final List<Task> proposalMessage,
            final List<Task> voteMessage,
            final List<Task> transactionEcho,
            final List<Task> messageEcho
    ) {
        this.proposalMessage = proposalMessage;
        this.voteMessage = voteMessage;
        this.transactionEcho = transactionEcho;
        this.messageEcho = messageEcho;
    }
}
