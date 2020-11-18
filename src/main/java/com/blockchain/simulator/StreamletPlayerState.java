package com.blockchain.simulator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.LinkedHashMap;

public class StreamletPlayerState {
    final int playerId;
    final Map<Integer, List<StreamletBlock>> chains;

    public StreamletPlayerState(StreamletPlayer player) {
        playerId = player.getId();
        chains = new LinkedHashMap<>();
        fillHashMap(player);
    }

    public void fillHashMap(StreamletPlayer player) {
        for (Map.Entry<Integer, StreamletBlock> entry : player.blockMap.entrySet()) {
            final StreamletBlock block = entry.getValue();
            final int level = block.getLevel();
            if (!chains.containsKey(level)) {
                chains.put(level, new LinkedList<>());
            }
            chains.get(level).add(block);
        }
    }
}
