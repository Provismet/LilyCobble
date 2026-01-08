package com.provismet.cobblemon.lilycobble.networking.battle;

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext;
import com.cobblemon.mod.common.api.pokemon.status.Status;
import com.cobblemon.mod.common.battles.pokemon.BattlePokemon;
import com.cobblemon.mod.common.pokemon.status.PersistentStatusContainer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record BattlePokemonState (UUID uuid, double healthPercentage, Optional<String> status, Map<String, Integer> statChanges) {
    public static final Codec<BattlePokemonState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Uuids.CODEC.fieldOf("uuid").forGetter(BattlePokemonState::uuid),
        Codec.DOUBLE.fieldOf("health_percentage").forGetter(BattlePokemonState::healthPercentage),
        Codec.STRING.optionalFieldOf("status").forGetter(BattlePokemonState::status),
        Codec.unboundedMap(Codec.STRING, Codec.INT).fieldOf("stat_changes").forGetter(BattlePokemonState::statChanges)
    ).apply(instance, BattlePokemonState::new));

    public static final PacketCodec<RegistryByteBuf, BattlePokemonState> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC,
        BattlePokemonState::uuid,
        PacketCodecs.DOUBLE,
        BattlePokemonState::healthPercentage,
        PacketCodecs.optional(PacketCodecs.STRING),
        BattlePokemonState::status,
        PacketCodecs.map(Object2ObjectOpenHashMap::new, PacketCodecs.STRING, PacketCodecs.INTEGER),
        BattlePokemonState::statChanges,
        BattlePokemonState::new
    );

    public static BattlePokemonState of (BattlePokemon pokemon) {
        Map<String, Integer> statChanges = new HashMap<>();
        Collection<BattleContext> boosts = pokemon.getContextManager().get(BattleContext.Type.BOOST);
        Collection<BattleContext> unboosts = pokemon.getContextManager().get(BattleContext.Type.UNBOOST);

        if (boosts != null) {
            for (BattleContext boost : boosts) {
                statChanges.compute(boost.getId(),(key, value) -> (value == null ? 0 : value) + 1);
            }
        }

        if (unboosts != null) {
            for (BattleContext unboost : unboosts) {
                statChanges.compute(unboost.getId(),(key, value) -> (value == null ? 0 : value) - 1);
            }
        }

        return new BattlePokemonState(
            pokemon.getUuid(),
            (double)pokemon.getHealth() / pokemon.getMaxHealth(),
            Optional.ofNullable(pokemon.getEffectedPokemon().getStatus())
                .map(PersistentStatusContainer::getStatus)
                .map(Status::getShowdownName),
            statChanges
        );
    }

    public boolean isAlive () {
        return this.healthPercentage > 0;
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof BattlePokemonState(UUID otherUUID, double otherHealth, Optional<String> otherStatus, Map<String, Integer> otherStatChanges))) {
            return false;
        }

        return Double.compare(this.healthPercentage, otherHealth) == 0
            && Objects.equals(this.uuid, otherUUID)
            && Objects.equals(this.status, otherStatus)
            && Objects.equals(this.statChanges, otherStatChanges);
    }

    @Override
    public int hashCode () {
        return Objects.hash(this.uuid, this.healthPercentage, this.status, this.statChanges);
    }
}
