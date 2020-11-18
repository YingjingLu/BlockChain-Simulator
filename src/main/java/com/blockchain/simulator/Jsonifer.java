package com.blockchain.simulator;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Jsonifer {
    public final String traceRootPath;

    public Jsonifer(final String traceRootPath) {
        this.traceRootPath = traceRootPath;
    }

    public JSONObject fileToJSONObject(final String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader(path);
        return (JSONObject) parser.parse(fileReader);
    }

    public void jsonObjectToFile(final JSONObject jsonObject, final String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        JSONValue.writeJSONString(jsonObject, writer);
    }

    public void jsonArrayToFile(final JSONArray jsonArray, final String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        JSONValue.writeJSONString(jsonArray, writer);
    }

    public String getConfigPath() {
        return traceRootPath + "/" + "config.json";
    }

    public String getMessageTracePath(final int round) {
        final String fileName;
        if (round == -1) {
            fileName = "init";
        } else {
            assert round >= 0 : "Round number should be greater or equal to 0";
            fileName = Integer.toString(round);
        }
        return traceRootPath + "/" + "message_trace/" + fileName + ".json";
    }

    public String getStateTracePath(final int round) {
        final String fileName;
        if (round == -1) {
            fileName = "init";
        } else {
            assert round >= 0 : "Round number should be greater or equal to 0";
            fileName = Integer.toString(round);
        }
        return traceRootPath + "/" + "player_state_trace/" + fileName + ".json";
    }

    public boolean parseBool(final JSONObject jsonObject, final String key) {
        return jsonObject.get(key).toString().equals("true");
    }
}