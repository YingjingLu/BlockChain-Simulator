package com.blockchain.simulator;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.lang.StringBuilder;

@Getter
public abstract class Message {
    @Setter protected String signature;
    @Setter int round;
    protected final List<Bit> message;
    protected final int fromPlayerId;
    protected final int toPlayerId;

    public Message(final int inRound, final List<Bit> inMessage, final int inFromPlayerId, final int inToPlayerId) {
        round = inRound;
        message = inMessage;
        fromPlayerId = inFromPlayerId;
        toPlayerId = inToPlayerId;
        signature = "";
    }

    public abstract Message deepCopy();

    public String messageToString() {
        StringBuilder builder = new StringBuilder();
        for (Bit i : message) {
            switch (i) {
                case FLOOR:
                    builder.append("2");
                    break;
                case ZERO:
                    builder.append("0");
                    break;
                case ONE:
                    builder.append("1");
                    break;
                default:
                    System.out.println("Error: not a valid bit type");
                    break;
            }
        }
        return builder.toString();
    }
}
