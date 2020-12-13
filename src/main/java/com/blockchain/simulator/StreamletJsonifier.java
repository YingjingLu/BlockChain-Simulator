package com.blockchain.simulator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.IllegalArgumentException;
import org.json.simple.parser.ParseException;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.Collections;

public class StreamletJsonifier extends Jsonifer {
    private final StreamletRoundSimulator roundSimulator;
    public StreamletJsonifier(final StreamletRoundSimulator roundSimulator, final String traceRootPath) {
        super(traceRootPath);
        this.roundSimulator = roundSimulator;
        final String proposalFolder = traceRootPath + "/" + "proposal_trace/";
        createFolderIfNotExists(proposalFolder);
    }

    public StreamletConfig getConfig() throws IOException, ParseException, IllegalArgumentException {
        final String configPath = getConfigPath();
        JSONObject configObject = fileToJSONObject(configPath);
        if (!configObject.containsKey("streamlet_config")) {
            throw new IllegalArgumentException("Streamlet Config file should contain streamlet_config key");
        }
        JSONObject streamletConfigObject = (JSONObject) configObject.get("streamlet_config");
        return jsonObjectToConfig(streamletConfigObject);
    }

    public String getProposalTracePath(final int round) {
        return traceRootPath + "/proposal_trace/" + round + ".json";
    }

    public boolean proposalExistsForRound(final int round) {
        return fileExists(getProposalTracePath(round));
    }

    public StreamletBlock getRoundProposal(final int round) throws IOException, ParseException {
        final StreamletBlock proposal;
        final String path = getProposalTracePath(round);
        if (!proposalExistsForRound(round)) {
            return null;
        }
        JSONObject jsonObject = fileToJSONObject(path);
        proposal = jsonObjectToBlock(jsonObject);
        return proposal;
    }

    public void writeRoundProposal(final int round, final StreamletBlock blockProposal) throws IOException {
        final String path = getProposalTracePath(round);
        jsonObjectToFile(blockToJSONObject(blockProposal), path);
    }

    public StreamletMessageTrace getRoundMessageTrace(final int round)
            throws IOException, ParseException, IllegalArgumentException {
        final List<Task> proposalTaskList;
        final List<Task> voteTaskList;
        final List<Task> broadcastInputTaskList;
        final List<Task> messageEchoList;


        final String path = getMessageTracePath(round);
        if (!hasMessageTrace(round)) {
            return null;
        }

        JSONObject jsonObject = fileToJSONObject(path);

        if (jsonObject.containsKey("proposal_task")) {
            proposalTaskList = new LinkedList<>();
            JSONArray arr = (JSONArray) jsonObject.get("proposal_task");
            for (Object obj : arr) {
                proposalTaskList.add(jsonObjectToTask((JSONObject) obj));
            }
        } else {
            proposalTaskList = null;
        }

        if (jsonObject.containsKey("vote_task")) {
            voteTaskList = new LinkedList<>();
            JSONArray arr = (JSONArray) jsonObject.get("vote_task");
            for (Object obj : arr) {
                voteTaskList.add(jsonObjectToTask((JSONObject) obj));
            }
        } else {
            voteTaskList = null;
        }

        if (jsonObject.containsKey("input_echo")) {
            broadcastInputTaskList = new LinkedList<>();
            JSONArray arr = (JSONArray) jsonObject.get("input_echo");
            for (Object obj : arr) {
                broadcastInputTaskList.add(jsonObjectToTask((JSONObject) obj));
            }
            // hard code every input with a global magic number to indicate it is a input message
            // TODO: fix this hard coding
            for (Task task: broadcastInputTaskList) {
                task.getMessage().setRound(Globals.streamletInputMessageRound);
            }
        } else {
            broadcastInputTaskList = null;
        }

        if (jsonObject.containsKey("message_echo")) {
            messageEchoList = new LinkedList<>();
            JSONArray arr = (JSONArray) jsonObject.get("message_echo");
            for (Object obj : arr) {
                messageEchoList.add(jsonObjectToTask((JSONObject) obj));
            }
        } else {
            messageEchoList = null;
        }

        return new StreamletMessageTrace(
                proposalTaskList,
                voteTaskList,
                broadcastInputTaskList,
                messageEchoList
        );
    }


    public StreamletConfig jsonObjectToConfig(JSONObject jsonObject) throws IllegalArgumentException {
        final int round, numTotalPlayer, numCorruptPlayer, maxDelay;
        final boolean useTrace;
        JSONArray level1InputArray;

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
        if (jsonObject.containsKey("max_delay")) {
            maxDelay = Integer.parseInt(jsonObject.get("max_delay").toString());
        } else {
            throw new IllegalArgumentException("Config file should contain max_delay");
        }
        if (jsonObject.containsKey("inputs")) {
            level1InputArray = (JSONArray) jsonObject.get("inputs");
        } else {
            level1InputArray = null;
        }

        // parse messages, if there is no message for current round, then insert an empty array
        final List<List<StreamletMessage>> inputMessageList = new LinkedList<>();
        for (int curRound = 0; curRound < round; curRound ++) {
            List<StreamletMessage> roundMessageList = new LinkedList<>();
            if (level1InputArray != null && level1InputArray.size() > curRound) {
                JSONArray level2Array = (JSONArray) level1InputArray.get(curRound);
                for (Object obj : level2Array) {
                    StreamletMessage message = jsonObjectToMessage((JSONObject) obj);
                    // mark this aas input message
                    message.setRound(Globals.streamletInputMessageRound);
                    roundMessageList.add(message);
                }
            }
            inputMessageList.add(roundMessageList);
        }
        return new StreamletConfig(
                round,
                numTotalPlayer,
                numCorruptPlayer,
                useTrace,
                maxDelay,
                inputMessageList
        );
    }

    public void writeMessageTrace(
            final int round,
            final List<Task> proposalTaskList,
            final List<Task> proposalVoteList,
            final List<Task> broadcastInputTaskList,
            final List<Task> echoMessageTaskList) throws IOException {
        final JSONObject traceObject = new JSONObject();
        final JSONArray proposalTaskArray = new JSONArray();
        for(Task t : proposalTaskList) {
            proposalTaskArray.add(taskToJSONObject(t));
        }
        traceObject.put("proposal_task", proposalTaskArray);

        final JSONArray voteTaskArray = new JSONArray();
        for (Task t : proposalVoteList) {
            voteTaskArray.add(taskToJSONObject(t));
        }
        traceObject.put("vote_task", voteTaskArray);

        final JSONArray inputEchoTaskArray = new JSONArray();
        for (Task t : broadcastInputTaskList) {
            inputEchoTaskArray.add(taskToJSONObject(t));
        }
        traceObject.put("input_echo", inputEchoTaskArray);

        final JSONArray echoMessageTaskArray = new JSONArray();
        for (Task t : echoMessageTaskList) {
            echoMessageTaskArray.add(taskToJSONObject(t));
        }
        traceObject.put("message_echo", echoMessageTaskArray);

        final String path = getMessageTracePath(round);

        jsonObjectToFile(traceObject, path);
    }

    public void writeStateTracePath (final int round) throws IOException {
        final String path = getStateTracePath(round);
        final JSONObject stateObject = new JSONObject();
        final JSONArray honestPlayerStateArray = new JSONArray();
        final JSONArray corruptPlayerStateArray = new JSONArray();
        for(Map.Entry<Integer, Player> entry : roundSimulator.corruptPlayerMap.entrySet()) {
            corruptPlayerStateArray.add(playerToJSONObject((StreamletPlayer) entry.getValue()));
        }
        for (Map.Entry<Integer, Player> entry : roundSimulator.honestPlayerMap.entrySet()) {
            honestPlayerStateArray.add(playerToJSONObject((StreamletPlayer) entry.getValue()));
        }
        stateObject.put("honest", honestPlayerStateArray);
        stateObject.put("corrupt", corruptPlayerStateArray);
        jsonObjectToFile(stateObject, path);
    }

    public JSONObject taskToJSONObject(Task task) {
        final JSONObject messageObject = messageToJSONObject((StreamletMessage) task.getMessage());
        final JSONObject taskObject = new JSONObject();
        taskObject.put("target_player", task.getTargetPlayer().getId());
        taskObject.put("message", messageObject);
        taskObject.put("delay", task.getDelay());
        return taskObject;
    }

    public JSONObject messageToJSONObject(StreamletMessage streamletMessage) {
        JSONObject messageObject = new JSONObject();
        messageObject.put("is_vote", streamletMessage.getIsVote());
        messageObject.put("approved", streamletMessage.approved.toString());
        messageObject.put("proposer_id", streamletMessage.getProposerId());
        messageObject.put("round", streamletMessage.getRound());
        messageObject.put("from_player_id", streamletMessage.getFromPlayerId());
        messageObject.put("to_player_id", streamletMessage.getToPlayerId());

        JSONArray signatureArray = new JSONArray();
        for (String str : streamletMessage.getSignatures()) {
            signatureArray.add(str);
        }
        messageObject.put("signatures", signatureArray);

        JSONArray messageArray = new JSONArray();
        for (int b : streamletMessage.getMessage()) {
            messageArray.add(Integer.toString(b));
        }
        messageObject.put("message", messageArray);
        return messageObject;
    }

    public JSONObject blockToJSONObject(StreamletBlock streamletBlock) {
        JSONObject blockObject = new JSONObject();
        blockObject.put("round", streamletBlock.getEpoch());
        blockObject.put("proposer_id", streamletBlock.getProposerId());
        if (streamletBlock.getPrev() == null) {
            blockObject.put("prev", -1);
        } else {
            blockObject.put("prev", streamletBlock.getPrev().getEpoch());
        }

        blockObject.put("notarized", streamletBlock.getNotorized());
        blockObject.put("finalized", streamletBlock.getFinalized());
        blockObject.put("level", streamletBlock.getLevel());
        JSONArray message = new JSONArray();
        for(Integer b : streamletBlock.getMessage()) {
            message.add(Integer.toString(b));
        }
        blockObject.put("message", message);
        return blockObject;
    }

    public JSONObject playerToJSONObject(StreamletPlayer player) {
        JSONObject playerObject = new JSONObject();
        StreamletPlayerState playerState = new StreamletPlayerState(player);
        playerObject.put("player_id", playerState.playerId);

        JSONArray levelArray = new JSONArray();
        List<Integer> lArray = new LinkedList<>();
        for(Map.Entry<Integer, List<StreamletBlock>> entry : playerState.chains.entrySet()) {
            lArray.add(entry.getKey());
        }
        Collections.sort(lArray);
        for(int level : lArray) {
            JSONArray blockArray = new JSONArray();
            for (StreamletBlock block : playerState.chains.get(level)) {
                blockArray.add(blockToJSONObject(block));
            }
            levelArray.add(blockArray);
        }
        playerObject.put("chains", levelArray);
        return playerObject;
    }

    public StreamletMessage jsonObjectToMessage(JSONObject jsonObject) throws IllegalArgumentException {
        final boolean isVote;
        final Bit approved;
        final int proposerId;
        final List<String> signatures;
        final int epoch;
        final List<Integer> message;
        final int fromPlayerId, toPlayerId;
        if (jsonObject.containsKey("is_vote")) {
            isVote = parseBool(jsonObject, "is_vote");
        } else {
            throw new IllegalArgumentException("Message Trace file should contain is_vote");
        }

        if (jsonObject.containsKey("approved")) {
            approved = Bit.stringToBit(jsonObject.get("approved").toString());
        } else {
            throw new IllegalArgumentException("Message Trace file should contain approved");
        }

        if (jsonObject.containsKey("proposer_id")) {
            proposerId = Integer.parseInt(jsonObject.get("proposer_id").toString());
        } else {
            throw new IllegalArgumentException("Message Trace file should contain proposer_id");
        }

        if (jsonObject.containsKey("round")) {
            epoch = Integer.parseInt(jsonObject.get("round").toString());
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
            message = new LinkedList<>();
            for (Object obj : messageList) {
                message.add(Integer.parseInt((String)obj));
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
          epoch,
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
        final List<Integer> message;
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

        if (jsonObject.containsKey("prev")) {
            prevBlockRound = Integer.parseInt(jsonObject.get("prev").toString());
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
            throw new IllegalArgumentException("Poropose block in Message Trace file should contain prev");
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
                message.add(Integer.parseInt((String)obj));
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

    public void printPlayerState(StreamletPlayer player) {
        System.out.println("Player: " + player.getId());
        for (Map.Entry<Integer, StreamletBlock> entry : player.chainTailMap.entrySet()) {
            StreamletBlock curBlock = entry.getValue();
            while (curBlock != null) {
                if (curBlock.getEpoch() == -1) {
                    System.out.print("G");
                } else {
                    System.out.print(curBlock.getEpoch());
                }

                if (curBlock.getNotorized()) {
                    System.out.print("*");
                }
                if (curBlock.getFinalized()) {
                    System.out.print("*");
                }
                System.out.print("-");
                curBlock = curBlock.getPrev();
            }
            System.out.println("|");
        }
    }
}
