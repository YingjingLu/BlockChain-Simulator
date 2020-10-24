package com.blockchain.simulator;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

public class DolevStrongPlayer extends Player {
    public Set<Bit> extractedSet;
    public List<DolevStrongMessage> prevRoundMessages;
    public List<DolevStrongMessage> curRoundMessages;
    public Bit outputBit = Bit.FLOOR;

    public DolevStrongPlayer(final int id, PlayerController playerController) {
        super(id, playerController);
        extractedSet = new HashSet<>();
        prevRoundMessages = new LinkedList<DolevStrongMessage>();
        curRoundMessages = new LinkedList<DolevStrongMessage>();
    }

    public void receiveMessage(final Message message, int round) {
        curRoundMessages.add((DolevStrongMessage) message);
    }

    public void beginRound(final int round) {
        List<DolevStrongMessage> tmpList = curRoundMessages;
        curRoundMessages = prevRoundMessages;
        prevRoundMessages = tmpList;
        curRoundMessages.clear();
    }

    public void setOutputBit(Bit b) {
        outputBit = b;
    }

    public Bit getOutputBit() {
        return outputBit;
    }
}
