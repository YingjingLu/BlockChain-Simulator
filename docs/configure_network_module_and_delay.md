# How to Configure Network & Delay

The network module can be configured in two ways:

### Max Network Delay
In the `config.json` for the protocol, we can configure the `max_delay` key for the protocol config.

If this `max_delay = -1` tells the simulator that the protocol is partially synchronous then there is no maximum delay and the adversary can delay as much as it wants. In streamlet protocol, when the network is partially synchronous, the epoch length is set to two rounds.

If this is a non zero number, then all message delays will be set to this `max_delay` value despite the adversary set it initially before passing it to the network. **If not -1, please set this number to be a positive integer to avoid undefined behavior**. If running streamlet protocol, the epoch length is 2 times the `max_delay` as per protocol's definition.

### Confogure individual network delay.

Each message object geenrated, when passed to network, is stored inside a task object. The task object contains three attributes: 
```java
int delay; // number of rounds this messages will be delayed from current round
Message message; // the message object being sent
Player targetPlayer; // the player intend to receive this message
```

1. You can modify the code that specifies the delay for each message. When `PlayerController` generate `Task` object to store message to pass to the `NetworkController`, you can specify this `delay` attribute to a value you want. 

2. Of course, you can design a manual attack without modifying the code. You can do this by specifying the `delay` field in the `message_trace` json files and pass those message traces to the simulator. In this way you can do quick experiment of "what will happen if I delay this message in current round, and that message in the next round" type of attack.

JSON structure of a task object:
```json
{
    "target_player": "1",
    "message": message object json,
    "delay": "1"
}
```

Delay of 1 means that the message sent in current round will arrive at the beginning of the next round(if there is any). Delay of -1 means the message is being delayed infinitely. Delay of 0 is undefined behavior.
