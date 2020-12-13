# How to Configure Network & Delay

The network module can be configured from two aspects:

### Max Network Delay
In the `config.json` for the protocol, we can configure the `max_delay` key for the protocol config.

If this `max_delay = -1` then there is no maximum delay and the adversary can delay as much as it wants. 

If this is a non zero number, then all message delays will be ser to this `max_delay` value despite the adversary set it initially before passing it to the network.

### Confogure individual network delay.

Each message object geenrated, when passed to network, is stored inside a task object. The task object contains three attributes: 
```java
int delay; // number of rounds this messages will be delayed from current round
Message message; // the message object being sent
Player targetPlayer; // the player intend to receive this message
```

So when generating messages, wrap them in task objects in the `PlayerController`, you can specify this `delay` attribute to a value you want. 

Of course, if you want to design a fine-grained attack that configures delay for each messages, you can also specify the `delay` field in the `message_trace` json files and pass those message traces to the simulator:

JSON structure of a task object:
```json
{
    "target_player": "1",
    "message": message object json,
    "delay": "0"
}
```
