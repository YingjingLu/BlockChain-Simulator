package com.blockchain.simulator;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Task {
    private final Player targetPlayer;
    private final Message message;
}
