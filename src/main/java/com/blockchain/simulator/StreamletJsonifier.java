package com.blockchain.simulator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.json.simple.parser.ParseException;
import java.util.List;
import java.util.LinkedList;
import java.util.stream.Stream;

public class StreamletJsonifier extends Jsonifer {
    private final StreamletRoundSimulator roundSimulator;
    public StreamletJsonifier(final StreamletRoundSimulator roundSimulator, final String traceRootPath) {
        super(traceRootPath);
        this.roundSimulator = roundSimulator;
    }

    public StreamletConfig getConfig() throws IOException, ParseException, IllegalArgumentException {
        final String configPath = getConfigPath();
        JSONObject jsonObj = fileToJSONObject(configPath);
        return jsonObjectToConfig(jsonObj);
    }

    public StreamletMessageTrace getRoundMessageTrace(final int round)
            throws IOException, ParseException, IllegalArgumentException {
        final List<Task> proposalTaskList;
        final List<Task> voteTaskList;
        final int leader;
        final StreamletBlock proposal;

        final String path = getMessageTracePath(round);
        JSONObject jsonObject = fileToJSONObject(path);

        if (jsonObject.containsKey("leader")) {
            leader = Integer.parseInt(jsonObject.get("leader").toString());
        } else {
            throw new IllegalArgumentException("Message Trace file should contain leader");
        }

        if (jsonObject.containsKey("proposal")) {
            proposal = jsonObjectToBlock((JSONObject) jsonObject.get("proposal"));
        } else {
            throw new IllegalArgumentException("Message Trace file should contain proposal");
        }

        if (jsonObject.containsKey("proposal_task")) {
            JSONArray arr = (JSONArray) jsonObject.get("proposal_task");
            proposalTaskList = new LinkedList<>();
            for (Object obj : arr) {
                proposalTaskList.add(jsonObjectToTask((JSONObject) obj));
            }
        } else {
            throw new IllegalArgumentException("Message Trace file should contain proposal_message");
        }

        if (jsonObject.containsKey("vote_message")) {
            JSONArray arr = (JSONArray) jsonObject.get("vote_message");
            voteTaskList = new LinkedList<>();
            for (Object obj : arr) {
                voteTaskList.add(jsonObjectToTask((JSONObject) obj));
            }
        } else {
            throw new IllegalArgumentException("Message Trace file should contain vote_message");
        }

        return new StreamletMessageTrace(
                leader,
                proposal,
                proposalTaskList,
                voteTaskList
        );
    }


    public StreamletConfig jsonObjectToConfig(JSONObject jsonObject) throws IllegalArgumentException {
        final int round, numTotalPlayer, numCorruptPlayer;
        final boolean useTrace;
        if (jsonObject.containsKey("round")) {
            round = Integer.parseInt(jsonObject.get("round").toString());
        } else {
            throw new IllegalArgumentException("Config file should contain round");
        }
        if (jsonObject.containsKey("num_corrupt_player")) {
            numCorruptPlayer = Integer.parseInt(jsonObject.get("num_corrupt_player").toString());
        } else {
            throw new IllegalArgumentException("Config file should contain num_corrupt_player");
        }
        if (jsonObject.containsKey("num_total_player")) {
            numTotalPlayer = Integer.parseInt(jsonObject.get("num_total_player").toString());
        } else {
            throw new IllegalArgumentException("Config file should contain num_total_player");
        }
        if (jsonObject.containsKey("use_trace")) {
            useTrace = parseBool(jsonObject, "use_trace");
        } else {
            throw new IllegalArgumentException("Config file should contain use_trace");
        }
        return new StreamletConfig(
                round,
                numTotalPlayer,
                numCorruptPlayer,
                useTrace
        );
    }

    public JSONObject taskToJSONObject(Task task) {
        return null;
    }

    public JSONObject messageToJSONObject(StreamletMessage streamletMessage) {
        return null;
    }

    public JSONObject blockToJSONObject(StreamletBlock streamletBlock) {
        return null;
    }

    public StreamletMessage jsonObjectToMessage(JSONObject jsonObject) throws IllegalArgumentException {
        final boolean isVote;
        final Bit approved;
        final int proposerId;
        final List<String> signatures;
        final int round;
        final List<Bit> message;
        final int fromPlayerId, toPlayerId;
        if (jsonObject.containsKey("is_vote")) {
            isVote = parseBool(jsonObject, "is_vote");
        } else {
            throw new IllegalArgumentException("Message Trace file should contain is_vote");
        }

        if (jsonObject.containsKey("approved")) {
            approved = Bit.valueOf(jsonObject.get("approved").toString());
        } else {
            throw new IllegalArgumentException("Message Trace file should contain approved");
        }

        if (jsonObject.containsKey("proposer_id")) {
            proposerId = Integer.parseInt(jsonObject.get("proposer_id").toString());
        } else {
            throw new IllegalArgumentException("Message Trace file should contain proposer_id");
        }

        if (jsonObject.containsKey("round")) {
            round = Integer.parseInt(jsonObject.get("round").toString());
        } else {
            throw new IllegalArgumentException("Message Trace file should contain round");
        }

        if (jsonObject.containsKey("signatures")) {
            JSONArray signatureList = (JSONArray) (jsonObject.get("signatures"));
            signatures = new LinkedList<String>();
            for (Object obj : signatureList) {
                signatures.add((String)obj);
            }

        } else {
            throw new IllegalArgumentException("Message Trace file should contain signatures");
        }

        if (jsonObject.containsKey("message")) {
            JSONArray messageList = (JSONArray) (jsonObject.get("message"));
            message = new LinkedList<Bit>();
            for (Object obj : messageList) {
                message.add(Bit.valueOf((String)obj));
            }

        } else {
            throw new IllegalArgumentException("Message Trace file should contain message");
        }

        if (jsonObject.containsKey("from_player_id")) {
            fromPlayerId = Integer.parseInt(jsonObject.get("from_player_id").toString());
        } else {
            throw new IllegalArgumentException("Message Trace file should contain from_player_id");
        }

        if (jsonObject.containsKey("to_player_id")) {
            toPlayerId = Integer.parseInt(jsonObject.get("to_player_id").toString());
        } else {
            throw new IllegalArgumentException("Message Trace file should contain to_player_id");
        }
        StreamletMessage newMessage = new StreamletMessage(
          isVote,
          round,
          message,
          fromPlayerId,
          toPlayerId,
          proposerId
        );
        if (approved == Bit.ONE) {
            newMessage.setApprove();
        } else if (approved == Bit.ZERO) {
            newMessage.setReject();
        }
        newMessage.addSignatures(signatures);
        return newMessage;
    }

    public StreamletBlock jsonObjectToBlock(JSONObject jsonObject) {
        final int round, proposerId, prevBlockRound, level;
        final List<Bit> message;
        final boolean notarized, finalized;
        final StreamletBlock prevBlock;

        if (jsonObject.containsKey("proposer_id")) {
            proposerId = Integer.parseInt(jsonObject.get("proposer_id").toString());
        } else {
            throw new IllegalArgumentException("Poropose block in Message Trace file should contain proposer_id");
        }

        if (jsonObject.containsKey("level")) {
            level = Integer.parseInt(jsonObject.get("level").toString());
        } else {
            throw new IllegalArgumentException("Poropose block in Message Trace file should contain level");
        }

        if (jsonObject.containsKey("prevBlockRound")) {
            prevBlockRound = Integer.parseInt(jsonObject.get("prevBlockRound").toString());
            // get the prev block pointer from proposer
            final StreamletPlayer proposer;
            if (roundSimulator.honestPlayerMap.containsKey(proposerId)) {
                proposer = (StreamletPlayer)roundSimulator.honestPlayerMap.get(proposerId);
            } else {
                assert roundSimulator.corruptPlayerMap.containsKey(proposerId) : "proposer ID should be valid in trace";
                proposer = (StreamletPlayer)roundSimulator.corruptPlayerMap.get(proposerId);
            }
            assert proposer.blockMap.containsKey(prevBlockRound) : "Proposed block in trace should exist in proposer's chain";
            assert proposer.blockMap.get(prevBlockRound).getNotorized() : "Proposed block in trace should be notarlized";
            prevBlock = proposer.blockMap.get(prevBlockRound);
        } else {
            throw new IllegalArgumentException("Poropose block in Message Trace file should contain prevBlockRound");
        }

        if (jsonObject.containsKey("round")) {
            round = Integer.parseInt(jsonObject.get("round").toString());
        } else {
            throw new IllegalArgumentException("Poropose block in Message Trace file should contain round");
        }

        if (jsonObject.containsKey("message")) {
            JSONArray arr = (JSONArray)(jsonObject.get("message"));
            message = new LinkedList<>();
            for (Object obj : arr) {
                message.add(Bit.valueOf(((JSONObject)obj).toString()));
            }
        } else {
            throw new IllegalArgumentException("Poropose block in Message Trace file should contain message");
        }

        if (jsonObject.containsKey("notarized")) {
            notarized = parseBool(jsonObject, "notarized");
        } else {
            throw new IllegalArgumentException("Block proposal in Message Trace file should contain notarized");
        }

        if (jsonObject.containsKey("finalized")) {
            finalized = parseBool(jsonObject, "finalized");
        } else {
            throw new IllegalArgumentException("Block proposal in Message Trace file should contain finalized");
        }
        StreamletBlock block = new StreamletBlock(
                round,
                proposerId,
                message,
                prevBlock,
                level
        );
        if (notarized) { block.setNotorized(); }
        if (finalized) { block.setFinalized(); }
        return block;
    }

    public Task jsonObjectToTask(JSONObject jsonObject) {
        final int targetPlayerId;
        final StreamletPlayer targetPlayer;
        StreamletMessage message;
        final int delay;

        if (jsonObject.containsKey("target_player")) {
            targetPlayerId = Integer.parseInt(jsonObject.get("target_player").toString());
            if (roundSimulator.honestPlayerMap.containsKey(targetPlayerId)) {
                targetPlayer = (StreamletPlayer) roundSimulator.honestPlayerMap.get(targetPlayerId);
            } else {
                assert roundSimulator.corruptPlayerMap.containsKey(targetPlayerId) : "target player should exist for trace";
                targetPlayer = (StreamletPlayer) roundSimulator.corruptPlayerMap.get(targetPlayerId);
            }
        } else {
            throw new IllegalArgumentException("Task in Message Trace file should contain target_player");
        }

        if (jsonObject.containsKey("delay")) {
            delay = Integer.parseInt(jsonObject.get("delay").toString());
        } else {
            throw new IllegalArgumentException("Task in Message Trace file should contain delay");
        }

        if (jsonObject.containsKey("message")) {
            message = jsonObjectToMessage((JSONObject) jsonObject.get("message"));
        } else {
            throw new IllegalArgumentException("Task in Message Trace file should contain message");
        }

        return new Task(targetPlayer, message, delay);
    }
}
