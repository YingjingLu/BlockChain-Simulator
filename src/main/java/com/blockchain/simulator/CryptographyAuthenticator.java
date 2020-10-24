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

    public void fAuth(final Message message) {
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

    public boolean fVerify(final Message message, final String signature) {
        if (!signatureMap.containsKey(signature)) {
            return false;
        }
        return message.messageToString().equals(signatureMap.get(signature).messageToString());
    }
}
