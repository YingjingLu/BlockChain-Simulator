package com.blockchain.simulator;
import java.util.List;

public class StreamletMessageTrace {
    public final List<Task> proposalMessage;
    public final List<Task> voteMessage;
    public final List<Task> transactionEcho;
    public final List<Task> messageEcho;

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
