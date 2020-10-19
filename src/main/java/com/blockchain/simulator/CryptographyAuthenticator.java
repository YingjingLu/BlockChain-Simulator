package com.blockchain.simulator;
import java.util.Map;
import java.util.HashMap;
import java.lang.StringBuilder;

public class CryptographyAuthenticator {
    public static final String SPLITTER = ",";
    private final Map<String, Message> signatureMap;

    public CryptographyAuthenticator() {
        signatureMap = new HashMap<String, Message>();
    }

    public boolean fAuth(final Message message) {
        // verify that message should not have been verified before
        if (message.getSignature() != "" || signatureMap.containsKey(message.getSignature())) {
            return false;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message.getRound().toString());
        stringBuilder.append(SPLITTER);
        stringBuilder.append(message.messageToString());
        stringBuilder.append(SPLITTER);
        stringBuilder.append(message.getFromPlayerId().toString());
        stringBuilder.append(SPLITTER);
        stringBuilder.append(message.getToPlayerId().toString());

        message.setSignature(stringBuilder.toString());
        signatureMap.put(message.getSignature(), message);
        return true;
    }

    public boolean fVerify(final Message message) {
        return signatureMap.containsKey(message.getSignature());
    }
}
