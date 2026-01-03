package com.provismet.cobblemon.lilycobble.networking;

import com.provismet.cobblemon.lilycobble.networking.battle.BattleStatePacketS2C;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class LilyCobblePackets {
    private LilyCobblePackets () {}

    private static boolean registered = false;

    public static void register () {
        if (registered) return;
        registered = true;

        PayloadTypeRegistry.playS2C().register(BattleStatePacketS2C.ID, BattleStatePacketS2C.PACKET_CODEC);
    }
}
