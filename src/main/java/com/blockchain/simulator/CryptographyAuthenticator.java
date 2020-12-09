package com.blockchain.simulator;
import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;
import java.lang.Integer;

/**
 * Mimicing the crypto authentication methods. Mianly consist of FAuth to generate signature for a player on a message
 * or verify if a signature is valid
 */
public class CryptographyAuthenticator {
    public static final String SPLITTER = ",";
    // the map that stores all the valid signatures generated so far for verification
    private final Map<String, Message> signatureMap;

    /**
     * constructor
     */
    public CryptographyAuthenticator() {
        signatureMap = new HashMap<>();
    }

    /**
     * Authenticate Dolev Strong messages
     * @param message
     */
    public void dolevStrongFAuth(final DolevStrongMessage message) {
        assert message != null : "message for fAuth should not be null";
        // verify that message should not have been verified before
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message.getRound());
        stringBuilder.append(SPLITTER);
        stringBuilder.append(message.messageToString());
        stringBuilder.append(SPLITTER);
        stringBuilder.append(message.getFromPlayerId());
        stringBuilder.append(SPLITTER);
        stringBuilder.append(message.getToPlayerId());

        final String sign = stringBuilder.toString();
        message.addSignature(sign);
        signatureMap.put(sign, message);
    }

    /**
     * Authenticate streamlet messages
     * @param message
     */
    public void streamletFAuth(final StreamletMessage message) {
        assert message != null : "message for fAuth should not be null";
        // verify that message should not have been verified before
        final String sign = getStreamletFAuth(
                message.getIsVote(),
                message.getRound(),
                message.messageToString(),
                message.getFromPlayerId(),
                message.getToPlayerId(),
                message.getProposerId()
        );
        message.addSignature(sign);
        signatureMap.put(sign, message);
    }

    /**
     * TODO: Implement the authentication function to generate a unique signature and append that into message's signature
     *authenticate Sample Protocol messages
     * @param message
     */
    public void sampleProtocolFAuth(final SampleProtocolMessage message) {
    }

    /**
     * Generate and return a streamlet message
     * @param isVote
     * @param round
     * @param messageString
     * @param fromPlayerId
     * @param toPlayerId
     * @param proposerId
     * @return
     */
    public String getStreamletFAuth(
            final boolean isVote,
            final int round,
            final String messageString,
            final int fromPlayerId,
            final int toPlayerId,
            final int proposerId) {
        StringBuilder stringBuilder = new StringBuilder();
        if (isVote) {
            stringBuilder.append(1);
        } else {
            stringBuilder.append(0);
        }
        stringBuilder.append(SPLITTER);
        stringBuilder.append(round);
        stringBuilder.append(SPLITTER);
        stringBuilder.append(messageString);
        stringBuilder.append(SPLITTER);
        stringBuilder.append(fromPlayerId);
        stringBuilder.append(SPLITTER);
        stringBuilder.append(toPlayerId);
        stringBuilder.append(SPLITTER);
        stringBuilder.append(proposerId);


        return stringBuilder.toString();
    }

    public static DolevStrongMessage signatureToDolevStrongMessage(final String sign) {
        String[] rawArr = sign.split(SPLITTER, 0);
        return new DolevStrongMessage(Integer.parseInt(rawArr[0]), null, Integer.parseInt(rawArr[2]), Integer.parseInt(rawArr[3]));
    }

    /**
     * Parse streamlet signature to reconstruct the message
     * @param sign
     * @return
     */
    public static StreamletMessage signatureToStreamletMessage(final String sign) {
        String[] rawArr = sign.split(SPLITTER, 0);
        return new StreamletMessage(
                Integer.parseInt(rawArr[0]) == 1,
                Integer.parseInt(rawArr[1]),
                StreamletMessage.stringToMessage(rawArr[2]),
                Integer.parseInt(rawArr[3]),
                Integer.parseInt((rawArr[4])),
                Integer.parseInt(rawArr[5])
        );
    }

    /**
     * Verify if a signature is valid
     * @param message
     * @param signature
     * @return
     */
    public boolean fVerify(final Message message, final String signature) {
        if (!signatureMap.containsKey(signature)) {
            return false;
        }
        return message.messageToString().equals(signatureMap.get(signature).messageToString());
    }
}
