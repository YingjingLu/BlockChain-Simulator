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

    /**
     * Constructor, create folders needed
     * @param traceRootPath
     */
    public Jsonifer(final String traceRootPath) {
        this.traceRootPath = traceRootPath;
        // create directories if not exists
        final String messageFolder = traceRootPath + "/" + "message_trace/";
        final String stateFolder = traceRootPath + "/" + "player_state_trace/";
        createFolderIfNotExists(messageFolder);
        createFolderIfNotExists(stateFolder);
    }

    /**
     * Create folder of a given path
     * @param path
     */
    public void createFolderIfNotExists(final String path) {
        final File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }
    }

    /**
     * load json file into json object
     * @param path
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public JSONObject fileToJSONObject(final String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader(path);
        JSONObject res = (JSONObject) parser.parse(fileReader);
        fileReader.close();
        return res;
    }

    /**
     * Load file into json array
     * @param path
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public JSONArray fileToJSONArray(final String path) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        FileReader fileReader = new FileReader(path);
        JSONArray res = (JSONArray) parser.parse(fileReader);
        fileReader.close();
        return res;
    }

    /**
     * Dump json object into json file
     * @param jsonObject
     * @param path
     * @throws IOException
     */
    public void jsonObjectToFile(final JSONObject jsonObject, final String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write(jsonObject.toJSONString());
        writer.flush();
        writer.close();
    }

    /**
     * Dump json array into json file
     * @param jsonArray
     * @param path
     * @throws IOException
     */
    public void jsonArrayToFile(final JSONArray jsonArray, final String path) throws IOException {
        FileWriter writer = new FileWriter(path);
        writer.write(jsonArray.toJSONString());
        writer.flush();
        writer.close();
    }

    /**
     * IF there exists message trace file for a given run
     * @param round
     * @return
     */
    public boolean hasMessageTrace(final int round) {
        final String messageTracePath = getMessageTracePath(round);
        return fileExists(messageTracePath);
    }

    /**
     * If a folder or file exists
     * @param path
     * @return
     */
    public boolean fileExists(final String path) {
        final File f = new File(path);
        return f.exists();
    }

    /**
     * Get the config.json file path
     * @return
     */
    public String getConfigPath() {
        return traceRootPath + "/" + "config.json";
    }

    /**
     * Get the output file path
     * @return
     */
    public String getOutputPath() {
        return traceRootPath + "/" + "output.json";
    }

    /**
     * Get the confit gile path for app
     * @param traceRootPath
     * @return
     */
    public static String getConfigPathForApp(final String traceRootPath) {
        return traceRootPath + "/" + "config.json";
    }

    /**
     * Get the message trace file path for a given round
     * @param round
     * @return
     */
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

    /**
     * Get player state trace file path for a given round
     * @param round
     * @return
     */
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