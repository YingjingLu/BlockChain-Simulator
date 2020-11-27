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
        "sender": "0",
        "use_trace": true,
        "initial_bit": "-1" for random, "1" or "0" for defined,
        "max_delay": 9 // max number of round -1 for no limit
    },
    "streamlet_config": {
        "round": "10",
        "num_corrupt_player": "3",
        "num_total_player": "10",
        "use_trace": true,
        "max_delay": 9 // max number of round -1 for no limit
    },
    "new_protocol_config": {
        "round": "10",
        "num_corrupt_player": "3",
        "num_total_player": "10",
        "use_trace": true,
        "max_delay": 2
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
