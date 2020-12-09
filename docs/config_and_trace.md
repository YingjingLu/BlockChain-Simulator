# Config Trace & State File

In our simulator design, the simulator inputs and outputs for one protocol configuration we call it a ```running case``` is stored in a folder. This folder will contains the configurations of protocol input, the messages being generated during protocol execution period and the player states, and outputs. 

Te folder structure is shown as below:

## Config/trace folder structure:
```
.
+-- _config.json
+-- _message_trace
|   +-- 0.json
|   +-- 1.json
+-- _player_state_trace
|   +-- init.json
|   +-- 0.json
|   +-- 1.json
```

The `config.json` in the root directory of this case folder contains the configurations such as player number, and inputs to players for every round. This is required to start running the simulator.

The `/message_trace` folder contains messages being generated for every round during the protocol execution. For example `0.json` stores the messages being generated at round 0 among players. **In our simulator, every protocol's round index starts from 0**.

The `/player_state_trace` folder contains each player's state at the end of every round. `init.json` records all the player states during protocol initialization. `0.json` records states of each players at the end of round 0.

## config.json
* The `config.json` defines parameters this protocol takes in. Below is the json structure for `config.json`
* top level key `protocol` specifies the protocol this config is for for the simulator to do initialization. It can be `"dolev_strong"` or `"streamlet"` or `"new_protocol"` that you further define. If you specify `"dolev_strong"` then further `"dolev_strong_config"` must be present, if you specify `"streamlet"`, `"streamlet_config"` must be present and so on.
* `"dolev_strong_config"`, `"streamlet_config"` and other protocol dependent configurations must contains keys: `round, num_corrupt_player, num_total_player, use_trace, max_delay`, `inputs` is optional if the protocol does not receive any inputs at any round.
* `round`: for Dolev strong, this round is not including the zeroth round. So if `dolev_strong_config` with round of 3 that means there will be round `0,1,2,3` where round `0` is the round sender receives input and sends to other players. The round `3` is the round every player generates output. However, for `streamlet_config` round of 3 will include round `0,1,2` in the record.
* Player IDs generated will indexed from 0 and corrupt players will always be at later index. For example if you have 5 players and 3 of them are corrupt that will be player `0, 1` being honest and player with id `2, 3, 4` will be corrupt.
* `use_trace`:  true if we use messages defined in the message_trace folder for communication among players each round, if false, protocol do not use any message traces but use protocol's definition to generate and communicate messages.
* `max_delay`: max number of round -1 for no limit to delay. If this is set to a non-negative number all the message delays, despite being manually configured in message trace, will be capped by this max_delay.
* `inputs`: The 2D array of `message` objects for each protocol. First dimension is the round and second dimension is the message. So `inputs[0]` stands for all the messages for players in round 0, and so on. For streamlet, there will be only one message, so only `inputs[0][0]` will be cunted. If `inputs` is missing, the sender and the bit will be randomly generated. 

```
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
                    "from_player_id": "-1", // 
                    "to_player_id": "0" // for Dolev strong this is the designated sender
                }
            ]
        ]
    },
    // if key of protocol is streamlet, then this field must be defined
    "streamlet_config": {
        "round": "10",
        "num_corrupt_player": "3",
        "num_total_player": "10",
        "use_trace": true,
        "max_delay": 9,
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
    // customize this for your own protocol
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
```

## Message Trace json structure
Below defines the message trace json structure for one round (for example `/message_trace/0.json`), This can differ depending on protocol's need. These message traces can be either specified before simulation run or not. If you want to let simulator automatically generate message, leave no file there and the simulator will fill out messages as it runs, or you can choose to use the messages you write and pass that in to the simulator with `config` `use_trace=true`. You can also specify part of the keys for example in streamlet you can specify `proposal_task` and leave `vote_task` blank, the simulator will use the proposal task you defined and automatically generate the vote task messages.

### DolevStrongMessageTrace
Each of tasks in this array includes a message one player sends to another
```
[
    Task 1 Object,
    Task 2 object,
    ....
]
```

### StreamletMessageTrace
* `proposal_task` is the array of tasks, each of which includes a proposal message that epoch leader sends to the other players. Each message also represents its inplicit echoing.
* `vote_task` is an array of tasks, each of which includes a vote message that one player sends to another, each message also represents the inplicit echoing of this message among players.
* `broadcast_input` is the list of tasks each includes the inplicit echoing of the message when a player receives an input to broadcast it to other players
```
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
```

## Player State Trace json structure
Below is the state trace for the two protocols that records every player's key states for output by the end of each round.
### DolevStrongPlayerState
```
{
    "player_id": 1,
    "extracted_set": ["0", "1"]
}
```
### StreamletPlayerState
* Players are divided into honest and corrupt two groups depending on the config of the case.
* `chain` key contains the array of blocks that player have at the end of an epoch
* the chain is a 2D array. The first dimension is the block level, second dimension is each block. So `chain[0]` representsan array of all blocks with level 0, `chain[1]` is the array of all blocks of level 1 and so on.
```
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
```

## Supporting objects that message_trace and state_trace includes inside their structures

### Task Object json
* THe task object that above message trace and player traces used. This is the json serialized version of task object in the simulator. that represents the packet passed to `NetworkSimulator` to be processed in the network queue.

* when the delay is `-1` it means the message will be delayed infinitely.

```
{
    "target_player": "1",
    "message": message object json,
    "delay": "0", "1", ...
}
```

### DolevStrongMessage
```
{
    "round": "3",
    "message": ["0"],
    "signatures": ["string 1", "string 2", ...],
    "from_player_id": "3",
    "to_player_id": "5"
}
```

### StreamletBlock
```
{
    "round": "5",
    "proposer_id": "3",
    "message": ["0", "1"],
    "prev": "4",
    "notarized": false,
    "finalized": "false",
    "level": "34"
}
```

### StreamletMessage
```
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
```


