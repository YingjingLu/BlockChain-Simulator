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

public class StreamletCaseN4F0R11Test {
    public static final String CASE_NAME = "streamlet_n_4_f_0_r_11";

    private final String caseFolder;

    public StreamletCaseN4F0R11Test() {
        caseFolder = TestIO.getTargetCaseFolder(CASE_NAME);
    }

    @Before
    public void setUp() throws IOException, IllegalArgumentException, ParseException {
        // copy the case folder to the target directory
        TestIO.copyCaseIntoTestFolder(CASE_NAME);
    }

    @After
    public void tearDown() throws IOException{
        TestIO.deleteFolder(caseFolder);
    }

    @Test
    public void runStreamletCaseN4F011Test() throws IOException, IllegalArgumentException, ParseException {
        StreamletRoundSimulator simulator = new StreamletRoundSimulator(caseFolder);
        StreamletPlayerController playerController = simulator.playerController;
        StreamletJsonifier jsonifier = simulator.jsonifier;
        NetworkSimulator networkSimulator = simulator.networkSimulator;
        simulator.run();
        // check proposals
        assertTrue(jsonifier.proposalExistsForRound(0));
        assertTrue(jsonifier.proposalExistsForRound(2));
        assertTrue(jsonifier.proposalExistsForRound(4));
        assertTrue(jsonifier.proposalExistsForRound(6));
        assertTrue(jsonifier.proposalExistsForRound(8));
        assertTrue(jsonifier.proposalExistsForRound(10));

        StreamletBlock b0 = jsonifier.jsonObjectToBlock(jsonifier.fileToJSONObject(jsonifier.getProposalTracePath(0)));
        assertEquals(0, b0.getEpoch());
        assertEquals(0, b0.getProposerId());
        assertEquals(-1, b0.getPrev().getEpoch());

        StreamletBlock b1 = jsonifier.jsonObjectToBlock(jsonifier.fileToJSONObject(jsonifier.getProposalTracePath(2)));
        assertEquals(1, b1.getEpoch());
        assertEquals(1, b1.getProposerId());
        assertEquals(0, b1.getPrev().getEpoch());

        StreamletBlock b2 = jsonifier.jsonObjectToBlock(jsonifier.fileToJSONObject(jsonifier.getProposalTracePath(4)));
        assertEquals(2, b2.getEpoch());
        assertEquals(2, b2.getProposerId());
        assertEquals(1, b2.getPrev().getEpoch());

        StreamletBlock b3 = jsonifier.jsonObjectToBlock(jsonifier.fileToJSONObject(jsonifier.getProposalTracePath(6)));
        assertEquals(3, b3.getEpoch());
        assertEquals(3, b3.getProposerId());
        assertEquals(2, b3.getPrev().getEpoch());

        StreamletBlock b4 = jsonifier.jsonObjectToBlock(jsonifier.fileToJSONObject(jsonifier.getProposalTracePath(8)));
        assertEquals(4, b4.getEpoch());
        assertEquals(0, b4.getProposerId());
        assertEquals(3, b4.getPrev().getEpoch());

        StreamletBlock b5 = jsonifier.jsonObjectToBlock(jsonifier.fileToJSONObject(jsonifier.getProposalTracePath(10)));
        assertEquals(5, b5.getEpoch());
        assertEquals(1, b5.getProposerId());
        assertEquals(4, b5.getPrev().getEpoch());

        // check messages
        StreamletMessageTrace trace0 = jsonifier.getRoundMessageTrace(0);
        assertEquals(4, trace0.proposalMessage.size());
        assertEquals(0, trace0.voteMessage.size());
        assertEquals(0, trace0.transactionEcho.size());
        assertEquals(0, trace0.messageEcho.size());

        StreamletMessageTrace trace1 = jsonifier.getRoundMessageTrace(1);
        assertEquals(0, trace1.proposalMessage.size());
        assertEquals(16, trace1.voteMessage.size());
        assertEquals(0, trace1.transactionEcho.size());
        assertEquals(16, trace1.messageEcho.size());

        StreamletMessageTrace trace2 = jsonifier.getRoundMessageTrace(2);
        assertEquals(4, trace2.proposalMessage.size());
        assertEquals(0, trace2.voteMessage.size());
        assertEquals(0, trace2.transactionEcho.size());
        assertEquals(64, trace2.messageEcho.size());

        StreamletMessageTrace trace3 = jsonifier.getRoundMessageTrace(3);
        assertEquals(0, trace3.proposalMessage.size());
        assertEquals(16, trace3.voteMessage.size());
        assertEquals(0, trace3.transactionEcho.size());
        assertEquals(16, trace3.messageEcho.size());

    }
}
