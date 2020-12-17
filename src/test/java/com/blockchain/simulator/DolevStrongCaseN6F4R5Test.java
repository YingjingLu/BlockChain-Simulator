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

public class DolevStrongCaseN6F4R5Test {
    public static final String CASE_NAME = "dolevstrong_n_6_f_4_r_5";

    private final String caseFolder;

    public DolevStrongCaseN6F4R5Test() {
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
    public void runDolevStrongCaseN6F4R5Test() throws IOException, IllegalArgumentException, ParseException {
        DolevStrongRoundSimulator simulator = new DolevStrongRoundSimulator(caseFolder);
        DolevStrongPlayerController playerController = simulator.playerController;
        DolevStrongJsonifier jsonifier = simulator.jsonifier;
        NetworkSimulator networkSimulator = simulator.networkSimulator;
        simulator.run();
    }
}
