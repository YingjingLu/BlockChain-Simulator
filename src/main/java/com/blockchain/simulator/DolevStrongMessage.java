package com.blockchain.simulator;

import java.util.List;

public class DolevStrongMessage extends Message {

    public DolevStrongMessage(final int inRound, final List<Bit> inMessage, final int inFromPlayerId, final int inToPlayerId) {
        super(inRound, inMessage, inFromPlayerId, inToPlayerId);
    }

    public Message deepCopy() {
        return new DolevStrongMessage(round, message, fromPlayerId, toPlayerId);
    }
}
