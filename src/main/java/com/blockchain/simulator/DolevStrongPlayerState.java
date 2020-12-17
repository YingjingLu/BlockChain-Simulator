package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;

/**
 * Dolev Strong Player State holder object
 */
public class DolevStrongPlayerState {
    public List<Bit> extractedSet;
    public final int playerId;
    public DolevStrongPlayerState(DolevStrongPlayer player) {
        playerId = player.getId();
        extractedSet = new LinkedList<>();
        if (player.extractedSet.contains(Bit.ZERO)) {
            extractedSet.add(Bit.ZERO);
        }
        if (player.extractedSet.contains(Bit.ONE)) {
            extractedSet.add(Bit.ONE);
        }
        if (player.extractedSet.contains(Bit.FLOOR)) {
            extractedSet.add(Bit.FLOOR);
        }
    }
}
