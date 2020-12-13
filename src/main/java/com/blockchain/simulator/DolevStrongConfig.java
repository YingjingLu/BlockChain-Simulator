package com.blockchain.simulator;
import java.util.Random;
import java.util.List;
import java.util.LinkedList;

/**
 * Data holder class for Dolev Strong specific configurations
 */
public class DolevStrongConfig {
    public final int round;
    public final int numCorruptPlayer;
    public final int numTotalPlayer;
    public final int maxDelay;
    final boolean useTrace;
    final List<List<DolevStrongMessage>> inputMessageList;

    /**
     * Constructor
     * @param round
     * @param numCorruptPlayer
     * @param numTotalPlayer
     * @param maxDelay
     * @param useTrace
     * @param initialBitMessage
     */
    public DolevStrongConfig(
            final int round,
            final int numCorruptPlayer,
            final int numTotalPlayer,
            final int maxDelay,
            final boolean useTrace,
            final DolevStrongMessage initialBitMessage) {
        this.round = round;
        this.numCorruptPlayer = numCorruptPlayer;
        this.numTotalPlayer = numTotalPlayer;
        this.maxDelay = maxDelay;
        this.useTrace = useTrace;

        // if the initial bit message is not given, randomize sender and input bit
        final DolevStrongMessage newInitialBitMessage;
        if (initialBitMessage == null) {
            List<Bit> message = new LinkedList<>();
            message.add(randomizeInitialBit());
            newInitialBitMessage = new DolevStrongMessage(
                    0,
                    message,
                    -1,
                    randomizeSenderId()
            );
        }
        else {
            // if sender id is set to -1, then randomize a legit sender id
            newInitialBitMessage = initialBitMessage;
            if (newInitialBitMessage.getToPlayerId() == -1) {
                newInitialBitMessage.setToPlayerId(randomizeSenderId());
            }
        }
        inputMessageList = new LinkedList<>();
        final List<DolevStrongMessage> initialMessageList = new LinkedList<>();
        initialMessageList.add(newInitialBitMessage);
        inputMessageList.add(initialMessageList);
    }

    /**
     * Randomly generate a sender id among all player ids
     * @return
     */
    public int randomizeSenderId() {
        return new Random().nextInt(numTotalPlayer);
    }

    /**
     * Randomly generate an initial bit from 0, 1
     * @return
     */
    public Bit randomizeInitialBit() {
        final int random = new Random().nextInt(2);
        if (random == 0) {
            return Bit.ZERO;
        }
        return Bit.ONE;
    }
}
