package com.blockchain.simulator;
import java.util.Comparator;

public class StreamletBlockSortByRound implements Comparator<StreamletBlock>{
    public int compare(StreamletBlock a, StreamletBlock b)
    {
        return a.getRound() - b.getRound();
    }
}
