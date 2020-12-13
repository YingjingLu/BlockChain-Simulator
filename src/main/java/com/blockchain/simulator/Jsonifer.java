package com.blockchain.simulator;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * File IO
 */
public class Jsonifer {
    public final String traceRootPath;

    public Jsonifer(final String traceRootPath) {
        this.traceRootPath = traceRootPath;
        // create directories if not exists
        final String messageFolder = traceRootPath + "/" + "message_trace/";
        final String stateFolder = traceRootPath + "/" + "player_state_trace/";
        createFolderIfNotExists(messageFolder);
        createFolderIfNotExists(stateFolder);
    }

    public void createFolderIfNotExists(final String path) {
        final File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    public JSONObject fileToJSONObject(final String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader(path);
        JSONObject res = (JSONObject) parser.parse(fileReader);
        fileReader.close();
        return res;
    }

    public JSONArray fileToJSONArray(final String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader(path);
        JSONArray res = (JSONArray) parser.parse(fileReader);
        fileReader.close();
        return res;
    }

    public void jsonObjectToFile(final JSONObject jsonObject, final String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write(jsonObject.toJSONString());
        writer.flush();
        writer.close();
    }

    public void jsonArrayToFile(final JSONArray jsonArray, final String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write(jsonArray.toJSONString());
        writer.flush();
        writer.close();
    }

    public boolean hasMessageTrace(final int round) {
        final String messageTracePath = getMessageTracePath(round);
        return fileExists(messageTracePath);
    }

    public boolean fileExists(final String path) {
        final File f = new File(path);
        return f.exists();
    }

    public String getConfigPath() {
        return traceRootPath + "/" + "config.json";
    }

    public String getOutputPath() {
        return traceRootPath + "/" + "output.json";
    }

    public static String getConfigPathForApp(final String traceRootPath) {
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