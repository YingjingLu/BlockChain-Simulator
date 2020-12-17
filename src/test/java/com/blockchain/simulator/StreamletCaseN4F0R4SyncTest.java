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

public class StreamletCaseN4F0R4SyncTest {

    public static final String CASE_NAME = "streamlet_n_4_f_0_r_4_sync";

    private final String caseFolder;

    public StreamletCaseN4F0R4SyncTest() {
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
    public void runStreamletCaseN4F0R4SyncTest() throws IOException, IllegalArgumentException, ParseException {
        StreamletRoundSimulator simulator = new StreamletRoundSimulator(caseFolder);
        StreamletPlayerController playerController = simulator.playerController;
        StreamletJsonifier jsonifier = simulator.jsonifier;
        NetworkSimulator networkSimulator = simulator.networkSimulator;
        simulator.run();

    }
}
