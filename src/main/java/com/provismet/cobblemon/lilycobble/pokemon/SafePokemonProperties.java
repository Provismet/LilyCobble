package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

/**
 * It is not safe to use the PokemonProperties codec in datapacks because it gets validated too early and will drop all datapack-specific
 * data (such as species, forms, etc).
 * <p>
 * This is a safe string-wrapper that can be converted into PokemonProperties on demand.
 */
public record SafePokemonProperties (String underlying) {
    public static final Codec<SafePokemonProperties> CODEC = Codec.STRING.xmap(SafePokemonProperties::new, SafePokemonProperties::underlying);
    public static final PacketCodec<ByteBuf, SafePokemonProperties> PACKET_CODEC = PacketCodecs.STRING.xmap(SafePokemonProperties::new, SafePokemonProperties::underlying);

    public SafePokemonProperties (PokemonProperties properties) {
        this(properties.asString(" "));
    }

    public PokemonProperties toPokemonProperties (String delimiter, String assigner) {
        return PokemonProperties.Companion.parse(this.underlying, delimiter, assigner);
    }

    public PokemonProperties toPokemonProperties (String delimiter) {
        return PokemonProperties.Companion.parse(this.underlying, delimiter);
    }

    public PokemonProperties toPokemonProperties () {
        return PokemonProperties.Companion.parse(this.underlying);
    }
}
