package com.blockchain.simulator;

import java.util.Map;

public class DolevStrongHonestPlayerController extends PlayerController{

    public DolevStrongHonestPlayerController(
            final NetworkSimulator networkSimulator,
            final Map<Integer, Player> honestPlayerMap,
            final Map<Integer, Player> corruptPlayerMap) {
        super(networkSimulator, honestPlayerMap, corruptPlayerMap);
    }

    public void sendMessagesToOtherPlayersViaNetwork(final int round) {

    }

    public void endRoundForPlayers(final int round) {

    }

    public void playerSendMessageToOtherPlayers(final int round) {

    }

    public void createOutputForEveryPlayer(final int round) {

    }

    public void createOutputForPlayer(final int round, final int playerId) {

    }
}
