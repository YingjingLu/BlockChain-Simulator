# Config/trace folder structure:
.
+-- _config.json
+-- _message_trace
|   +-- 0.json
|   +-- 1.json
+-- _player_state_trace
|   +-- init.json
|   +-- 0.json
|   +-- 1.json

## Initial configs
{
    "protocol": "dolev_strong" or "streamlet" or "new_protocol",
    "dolev_strong_config": {
        "round": "3",
        "num_corrupt_player": "3",
        "num_total_player": "10",
        "use_trace": true,
        "max_delay": 1 // max number of round -1 for no limit,
        // if this field is not given, then it is gonna have random sender with a random bit
        "inputs": [ 
            // 2D array of input for each round
            // each round is a separate array
            [ // this is the 0th array so for round zero
                {
                    "round": "0",
                    "message": ["0"], // "-1" for random, "1" or "0" for defined,
                    "signatures": [],
                    "from_player_id": "-1",
                    "to_player_id": "0" // for Dolev strong this is the designated sender
                }
            ]
        ]
    },
    "streamlet_config": {
        "round": "10",
        "num_corrupt_player": "3",
        "num_total_player": "10",
        "use_trace": true,
        "max_delay": 9 // max number of round -1 for no limit
        "inputs": [ // if not given then player by default has a dummy input message, do not need to specify here
            [
                {
                    "is_vote": false,
                    "approved": "0" or "1" or "2",
                    "proposer_id": "0",
                    "signatures": [],
                    "round": "0",
                    "message": ["0", "1"],
                    "from_player_id": "-1",
                    "to_player_id": "6" // player which receives input
                }
            ]
        ]
    },
    "new_protocol_config": {
        // required arguments 
        "round": "10",
        "num_corrupt_player": "3",
        "num_total_player": "10",
        "use_trace": true,
        "max_delay": 2,
        "inputs": [
            [
                new_message_message_object    
            ]    
        ]
    }
}

### DolevStrongMessageTrace
[
    Task 1 Object,
    Task 2 object,
    ....
]

### DolevStrongMessage
{
    "round": "3",
    "message": ["0"],
    "signatures": ["string 1", "string 2", ...],
    "from_player_id": "3",
    "to_player_id": "5"
}

### DolevStrongPlayerState
{
    "player_id": 1,
    "extracted_set": ["0", "1"]
}

### StreamletMessageTrace
{
    "leader": "0",
    "proposal": block proposal block object json
    "proposal_task": [
        task 1 json,
        task 2 json,
        ...
    ],
    "vote_task": [
        task 1 json,
        task 2 json,
        ...
    ],
    "broadcast_input": [
        task 1 json,
        ...
    ]
}

### StreamletBlock
{
    "round": "5",
    "proposer_id": "3",
    "message": ["0", "1"],
    "prev": "4",
    "notarized": false,
    "finalized": "false",
    "level": "34"
}

### StreamletMessage
{
    "is_vote": true,
    "approved": "0" or "1" or "2",
    "proposer_id": "0",
    "signatures": [
        "signature1",
        "signature2",
        ...
    ],
    "round": "3",
    "message": ["0", "1"],
    "from_player_id": "2",
    "to_player_id": "6"
}

### Task
{
    "target_player": "1",
    "message": message object json,
    "delay": "0", "1", ...
}

### StreamletPlayerState
{
    "honest": [
        {
            "player_id": "5",
            "chains": [ // each key represent the level
                [
                    streamlet block object 1,
                    streamlet block object 2,
                    ...
                ],
                [
                    streamlet block object 1,
                    streamlet block object 2,
                    ...
                ],
                ...
            ]
        },
        ...
    ],
    "corrupt": [
        {
            "player_id": "5",
            "chains": [ // each key represent the level
                [
                    streamlet block object 1,
                    streamlet block object 2,
                    ...
                ],
                [
                    streamlet block object 1,
                    streamlet block object 2,
                    ...
                ],
                ...
            ]
        },
        ...
    ]
}

# Feature TODOs:
- [x] Implement inputs in the config: setup format, jsonifier, round simulator, player controller, player method
- [x] Redesign a streamlet attack
- [x] UI for upload and download for execution
- [x] Running Case samples 3 for Dolev Strong 3 for streamlet
- [x] UI for different node color
- [x] Environmental variable for java execution

# Report TODOs:
- [x] Streamlet attack case
- [ ] Sample cases illustrations
- [ ] Class interface structures
- [ ] Major class Java docs
- [ ] Input configuration and trace formats 
- [ ] How to write honest new protocol
- [ ] How to change attack strategy for Dolev Strong and Streamlet
- [ ] How to configure network Model and Delay Parameters
- [ ] How to run Simulator (Command line + UI)
- [ ] How to visualize Streamlet through UI
- [ ] How to install and set up (Simulator + UI)
- [ ] Javadoc for Streamlet and Dolev Strong
- [ ] Root directory: Overview of Folder structure and report

# Remember the message trace represent original message and all implicit echoing