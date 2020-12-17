package com.blockchain.simulator;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

/**
 * Dolev Strong Player
 */
public class DolevStrongPlayer extends Player {
    public Set<Bit> extractedSet;
    public List<DolevStrongMessage> curRoundMessages;
    public Bit outputBit = Bit.FLOOR;

    public DolevStrongPlayer(final int id, PlayerController playerController) {
        super(id, playerController);
        extractedSet = new HashSet<>();
        curRoundMessages = new LinkedList<>();
    }

    /**
     * Receive inptu from player controller, add to cur round input message list
     */
    public void receiveInput(final Message inputMessage) {
        curRoundMessages.add((DolevStrongMessage) inputMessage);
    }

    /**
     * Receive message from network, add to cur round message list to process
     */
    public void receiveMessage(final Message message, int round) {
        curRoundMessages.add((DolevStrongMessage) message);
    }

    /**
     * Do nothing
     */
    public void beginRound(final int round) {

    }

    /**
     * Clear cur messages 
     */
    public void endRound() {
        curRoundMessages.clear();
    }

    /**
     * Set output bit given by player controller
     */
    public void setOutputBit(Bit b) {
        outputBit = b;
    }

    /**
     * Output bit getter
     */
    public Bit getOutputBit() {
        return outputBit;
    }
}
