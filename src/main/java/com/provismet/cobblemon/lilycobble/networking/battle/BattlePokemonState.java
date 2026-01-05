package com.provismet.cobblemon.lilycobble.networking.battle;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.status.Status;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.lilycobble.util.CobblemonCodecs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public record BattlePokemonState (UUID uuid, double healthPercentage, Optional<String> status, Map<Stat, Integer> statChanges) {
    public static final Codec<BattlePokemonState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Uuids.CODEC.fieldOf("uuid").forGetter(BattlePokemonState::uuid),
        Codec.DOUBLE.fieldOf("health_percentage").forGetter(BattlePokemonState::healthPercentage),
        Codec.STRING.optionalFieldOf("status").forGetter(BattlePokemonState::status),
        Codec.unboundedMap(Stat.getALL_CODEC(), Codec.INT).fieldOf("stat_changes").forGetter(BattlePokemonState::statChanges)
    ).apply(instance, BattlePokemonState::new));

    public static final PacketCodec<RegistryByteBuf, BattlePokemonState> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC,
        BattlePokemonState::uuid,
        PacketCodecs.DOUBLE,
        BattlePokemonState::healthPercentage,
        PacketCodecs.optional(PacketCodecs.STRING),
        BattlePokemonState::status,
        PacketCodecs.map(Object2ObjectOpenHashMap::new, CobblemonCodecs.STAT_PACKET_CODEC, PacketCodecs.INTEGER),
        BattlePokemonState::statChanges,
        BattlePokemonState::new
    );

    public static BattlePokemonState of (BattlePokemon pokemon) {
        return new BattlePokemonState(
            pokemon.getUuid(),
            (double)pokemon.getHealth() / pokemon.getMaxHealth(),
            Optional.ofNullable(pokemon.getEffectedPokemon().getStatus())
                .map(PersistentStatusContainer::getStatus)
                .map(Status::getShowdownName),
            pokemon.getStatChanges()
        );
    }

    public boolean isAlive () {
        return this.healthPercentage > 0;
    }
}
