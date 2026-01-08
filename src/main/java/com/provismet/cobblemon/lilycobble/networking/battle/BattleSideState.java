package com.provismet.cobblemon.lilycobble.networking.battle;

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext;
import com.cobblemon.mod.common.battles.BattleSide;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record BattleSideState (List<String> sideEffects, List<BattleActorState> actors) {
    public static final Codec<BattleSideState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().optionalFieldOf("sided_effects", List.of()).forGetter(BattleSideState::sideEffects),
        BattleActorState.CODEC.listOf().optionalFieldOf("actors", List.of()).forGetter(BattleSideState::actors)
    ).apply(instance, BattleSideState::new));

    public static final PacketCodec<RegistryByteBuf, BattleSideState> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.STRING.collect(PacketCodecs.toList()),
        BattleSideState::sideEffects,
        BattleActorState.PACKET_CODEC.collect(PacketCodecs.toList()),
        BattleSideState::actors,
        BattleSideState::new
    );

    public static BattleSideState of (BattleSide side) {
        Set<String> sidedEffects = new HashSet<>();
        extractContext(sidedEffects, side, BattleContext.Type.HAZARD);
        extractContext(sidedEffects, side, BattleContext.Type.SCREEN);
        extractContext(sidedEffects, side, BattleContext.Type.TAILWIND);
        extractContext(sidedEffects, side, BattleContext.Type.MISC);

        List<BattleActorState> actors = Arrays.stream(side.getActors())
            .sorted(Comparator.comparing(actor -> actor.getUuid().toString()))
            .map(BattleActorState::of)
            .toList();

        return new BattleSideState(sidedEffects.stream().toList(), actors);
    }

    public List<BattlePokemonState> getPokemon () {
        return this.actors.stream()
            .flatMap(state -> state.team().stream())
            .toList();
    }

    public boolean isFor (PlayerEntity player) {
        return this.actors.stream().anyMatch(actor -> actor.isFor(player));
    }

    private static void extractContext (Collection<String> mutableCollection, BattleSide side, BattleContext.Type contextType) {
        Collection<BattleContext> fullContext = side.getContextManager().get(contextType);
        if (fullContext == null) return;

        fullContext.stream()
            .map(BattleContext::getId)
            .forEach(mutableCollection::add);
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof BattleSideState(List<String> otherEffects, List<BattleActorState> otherActors))) return false;
        return Objects.equals(this.sideEffects, otherEffects) && Objects.equals(this.actors, otherActors);
    }

    @Override
    public int hashCode () {
        return Objects.hash(this.sideEffects, this.actors);
    }
}
