package com.blockchain.simulator;
import java.util.Comparator;

public class StreamletBlockSortByEpoch implements Comparator<StreamletBlock>{
    public int compare(StreamletBlock a, StreamletBlock b)
    {
        return a.getEpoch() - b.getEpoch();
    }
}
