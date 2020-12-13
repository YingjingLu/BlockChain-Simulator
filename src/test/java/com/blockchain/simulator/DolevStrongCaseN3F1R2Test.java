package com.blockchain.simulator;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.json.simple.parser.ParseException;

public class DolevStrongCaseN3F1R2Test {
    public static final String CASE_NAME = "dolevstrong_n_3_f_1_r_2";

    private final String caseFolder;

    public DolevStrongCaseN3F1R2Test() {
        caseFolder = TestIO.getTargetCaseFolder(CASE_NAME);
    }

    @Before
    public void setUp() throws IOException, IllegalArgumentException, ParseException {
        // copy the case folder to the target directory
        TestIO.copyCaseIntoTestFolder(CASE_NAME);
    }

    @After
    public void tearDown() throws IOException{
        TestIO.deleteFolder(TestIO.getTargetCaseFolder(CASE_NAME));
    }

    @Test
    public void runDolevStrongcaseN3F1R2() throws IOException, IllegalArgumentException, ParseException {
        DolevStrongRoundSimulator simulator = new DolevStrongRoundSimulator(caseFolder);
        DolevStrongPlayerController playerController = simulator.playerController;
        DolevStrongJsonifier jsonifier = simulator.jsonifier;
        NetworkSimulator networkSimulator = simulator.networkSimulator;
        simulator.run();

        // check for message round 0
        JSONArray r0Array = jsonifier.fileToJSONArray(jsonifier.getMessageTracePath(0));
        assertEquals(3, r0Array.size());

        // check for message round 1
        JSONArray r1Array = jsonifier.fileToJSONArray(jsonifier.getMessageTracePath(1));
        assertEquals(9, r1Array.size());

        // check for message round 2
        JSONArray r2Array = jsonifier.fileToJSONArray(jsonifier.getMessageTracePath(2));
        assertEquals(24, r2Array.size());

        // check for outputs
        JSONObject outputObject = jsonifier.fileToJSONObject(jsonifier.getOutputPath());
        assertEquals("F", outputObject.get("0").toString());
        assertEquals("F", outputObject.get("1").toString());

    }
}
