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
     * Receive input add to input message list
     * @param inputMessage
     */
    public void receiveInput(final Message inputMessage) {
        curRoundMessages.add((DolevStrongMessage) inputMessage);
    }

    /**
     * Receive message, add to current round message list
     * @param message
     * @param round
     */
    public void receiveMessage(final Message message, int round) {
        curRoundMessages.add((DolevStrongMessage) message);
    }

    /**
     * Do nothing
     * @param round
     */
    public void beginRound(final int round) {

    }

    /**
     * Clear current round messages
     */
    public void endRound() {
        curRoundMessages.clear();
    }

    /**
     * Output bit setter
     * @param b
     */
    public void setOutputBit(Bit b) {
        outputBit = b;
    }

    /**
     * Output bit getter
     * @return
     */
    public Bit getOutputBit() {
        return outputBit;
    }
}
