package com.blockchain.simulator;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Message {
    @Setter private String signature;
    @Setter private Message prevMessage;
    @Setter private Message nextMessage;
    @Setter int round;
    private final String message;
    private final int fromPlayerId;
    private final int toPlayerId;

    public Message(final int inRound, final String inMessage, final int inFromPlayerId, final int inToPlayerId) {
        round = inRound;
        message = inMessage;
        fromPlayerId = inFromPlayerId;
        toPlayerId = inToPlayerId;
        prevMessage = null;
        nextMessage = null;
        signature = "";
    }

    public Message deepCopy() {
        final Message newMessage = new Message(round, message, fromPlayerId, toPlayerId);
        newMessage.setSignature(signature);
        newMessage.setPrevMessage(prevMessage);
        newMessage.setNextMessage(nextMessage);
        return newMessage;
    }
}
