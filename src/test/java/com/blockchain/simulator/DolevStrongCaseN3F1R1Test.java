package com.blockchain.simulator;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.json.simple.parser.ParseException;
public class DolevStrongCaseN3F1R1Test {

    public static final String CASE_NAME = "dolevstrong_n_3_f_1_r_1";

    private final String caseFolder;

    public DolevStrongCaseN3F1R1Test() {
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
    public void runDolevStrongcaseN3F1R1() throws IOException, IllegalArgumentException, ParseException {
        DolevStrongRoundSimulator simulator = new DolevStrongRoundSimulator(caseFolder);
        DolevStrongPlayerController playerController = simulator.playerController;
        CryptographyAuthenticator uthenticator = simulator.authenticator;
        NetworkSimulator networkSimulator = simulator.networkSimulator;
        simulator.run();
        assertTrue( true );
    }

}
