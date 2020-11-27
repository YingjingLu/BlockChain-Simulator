package com.blockchain.simulator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.json.simple.parser.ParseException;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Collections;

public class DolevStrongJsonifier extends Jsonifer {
    private final DolevStrongRoundSimulator roundSimulator;

    public DolevStrongJsonifier(final DolevStrongRoundSimulator roundSimulator, final String traceRootPath) {
        super(traceRootPath);
        this.roundSimulator = roundSimulator;
    }

    public DolevStrongConfig getConfig() throws IOException, ParseException, IllegalArgumentException {
        final String configPath = getConfigPath();
        JSONObject jsonObj = fileToJSONObject(configPath);
        JSONObject dolevStrongSJON = (JSONObject) jsonObj.get("dolev_strong_config");
        if (!dolevStrongSJON.containsKey("round")) {
            throw new IllegalArgumentException("Dolev Strong protocol's round argument should be specified");
        }
        if (!dolevStrongSJON.containsKey("num_corrupt_player")) {
            throw new IllegalArgumentException("Dolev Strong protocol's num_corrupt_player should be specified");
        }
        if (!dolevStrongSJON.containsKey("num_total_player")) {
            throw new IllegalArgumentException("Dolev Strong protocol's num_total_player should be specified");
        }
        if (!dolevStrongSJON.containsKey("sender")) {
            throw new IllegalArgumentException("Dolev Strong protocol's sender should be specified");
        }
        if (!dolevStrongSJON.containsKey("initial_bit")) {
            throw new IllegalArgumentException("Dolev Strong protocol's initial_bit should be specified");
        }

        DolevStrongConfig config = new DolevStrongConfig(
                Integer.parseInt(dolevStrongSJON.get("round").toString()),
                Integer.parseInt(dolevStrongSJON.get("num_corrupt_player").toString()),
                Integer.parseInt(dolevStrongSJON.get("num_total_player").toString()),
                Integer.parseInt(dolevStrongSJON.get("sender").toString()),
                Integer.parseInt(dolevStrongSJON.get("initial_bit").toString())
        );
        return config;
    }
}
