package com.blockchain.simulator;
import java.util.Comparator;

/**
 * Block comparator based on block epoch id/block if
 */
public class StreamletBlockSortByEpoch implements Comparator<StreamletBlock>{
    public int compare(StreamletBlock a, StreamletBlock b)
    {
        return a.getEpoch() - b.getEpoch();
    }
}
