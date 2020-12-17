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

public class DolevStrongGenerateInputTest {

    @Test
    public void randomizeGenerateNullMessage() {

        DolevStrongConfig config = new DolevStrongConfig(
                10,
                5,
                7,
                1,
                false,
                null
        );
        assertEquals(10, config.round);
        assertEquals(1, config.maxDelay);
        assertEquals(5, config.numCorruptPlayer);
        assertEquals(7, config.numTotalPlayer);
        assertTrue(config.inputMessageList != null);
        assertEquals(1, config.inputMessageList.size());
        assertTrue(config.inputMessageList.get(0) != null);
        DolevStrongMessage message = config.inputMessageList.get(0).get(0);
        assertEquals(0, message.getRound());
        assertEquals(1, message.getMessage().size());
        assertEquals(-1, message.getFromPlayerId());
        assertTrue(message.getToPlayerId() < config.numTotalPlayer);
        assertTrue(message.getToPlayerId() >= 0);
        assertTrue(message.getMessage().get(0) != Bit.FLOOR);
    }

    @Test
    public void randomizeGenerateMessageSender() {

        DolevStrongMessage prevMesssage = new DolevStrongMessage(
                0,
                null,
                -1,
                -1
        );
        DolevStrongConfig config = new DolevStrongConfig(
                10,
                5,
                7,
                1,
                false,
                prevMesssage
        );
        DolevStrongMessage postMessage = config.inputMessageList.get(0).get(0);
        assertEquals(0, postMessage.getRound());
        assertEquals(-1, postMessage.getFromPlayerId());
        assertTrue(postMessage.getToPlayerId() < config.numTotalPlayer);
        assertTrue(postMessage.getToPlayerId() >= 0);
    }
}
