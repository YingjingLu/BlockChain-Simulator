package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;

public class StreamletMessageTrace {
    public final int leader;
    public final StreamletBlock proposal;
    public final List<Task> proposalMessage;
    public final List<Task> voteMessage;
    public final List<Task> transactionBroadcast;

    public StreamletMessageTrace (
            final int leader,
            final StreamletBlock proposal,
            final List<Task> proposalMessage,
            final List<Task> voteMessage,
            final List<Task> transactionBroadcast
    ) {
        this.leader = leader;
        this.proposal = proposal;
        this.proposalMessage = proposalMessage;
        this.voteMessage = voteMessage;
        this.transactionBroadcast = transactionBroadcast;
    }

    public boolean containsBlockProposal() {
        return proposal != null;
    }

    public boolean containsProposalTrace() {
        return proposalMessage.size() != 0;
    }

    public boolean containsVoteTrace() {
        return voteMessage.size() != 0;
    }
}
