package com.provismet.cobblemon.lilycobble.networking;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.Identifier;

public interface CobblemonCodecs {
    PacketCodec<ByteBuf, Stat> STAT_PACKET_CODEC = Identifier.PACKET_CODEC.xmap(Cobblemon.INSTANCE.getStatProvider()::fromIdentifier, Stat::getIdentifier);
}
