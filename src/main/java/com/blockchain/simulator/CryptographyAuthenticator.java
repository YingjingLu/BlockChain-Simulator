package com.blockchain.simulator;
import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;
import java.lang.Integer;

public class CryptographyAuthenticator {
    public static final String SPLITTER = ",";
    private final Map<String, Message> signatureMap;

    public CryptographyAuthenticator() {
        signatureMap = new HashMap<>();
    }

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

    public static DolevStrongMessage signatureToDolevStringMessage(final String sign) {
        String[] rawArr = sign.split(SPLITTER, 0);
        return new DolevStrongMessage(Integer.parseInt(rawArr[0]), null, Integer.parseInt(rawArr[2]), Integer.parseInt(rawArr[3]));
    }

    public static StreamletMessage signatureToStreamletMessage(final String sign) {
        String[] rawArr = sign.split(SPLITTER, 0);
        return new StreamletMessage(
                Integer.parseInt(rawArr[0]) == 1,
                Integer.parseInt(rawArr[1]),
                Message.stringToBitMessage(rawArr[2]),
                Integer.parseInt(rawArr[3]),
                Integer.parseInt((rawArr[4])),
                Integer.parseInt(rawArr[5])
        );
    }

    public boolean fVerify(final Message message, final String signature) {
        if (!signatureMap.containsKey(signature)) {
            return false;
        }
        return message.messageToString().equals(signatureMap.get(signature).messageToString());
    }
}
