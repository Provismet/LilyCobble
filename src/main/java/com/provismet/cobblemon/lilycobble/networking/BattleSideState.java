package com.provismet.cobblemon.lilycobble.networking;

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext;
import com.cobblemon.mod.common.battles.BattleSide;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record BattleSideState (List<String> sideEffects, List<BattlePokemonState> pokemon) {
    public static final Codec<BattleSideState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().optionalFieldOf("sided_effects", List.of()).forGetter(BattleSideState::sideEffects),
        BattlePokemonState.CODEC.listOf().optionalFieldOf("pokemon", List.of()).forGetter(BattleSideState::pokemon)
    ).apply(instance, BattleSideState::new));

    public static final PacketCodec<RegistryByteBuf, BattleSideState> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.STRING.collect(PacketCodecs.toList()),
        BattleSideState::sideEffects,
        BattlePokemonState.PACKET_CODEC.collect(PacketCodecs.toList()),
        BattleSideState::pokemon,
        BattleSideState::new
    );

    public static BattleSideState of (BattleSide side) {
        Set<String> sidedEffects = new HashSet<>();
        extractContext(sidedEffects, side, BattleContext.Type.HAZARD);
        extractContext(sidedEffects, side, BattleContext.Type.SCREEN);
        extractContext(sidedEffects, side, BattleContext.Type.TAILWIND);
        extractContext(sidedEffects, side, BattleContext.Type.MISC);

        List<BattlePokemonState> pokemon = Arrays.stream(side.getActors())
            .flatMap(actor -> actor.getPokemonList().stream())
            .map(BattlePokemonState::of)
            .toList();

        return new BattleSideState(sidedEffects.stream().toList(), pokemon);
    }

    private static void extractContext (Collection<String> mutableCollection, BattleSide side, BattleContext.Type contextType) {
        Collection<BattleContext> fullContext = side.getContextManager().get(contextType);
        if (fullContext == null) return;

        fullContext.stream()
            .map(BattleContext::getId)
            .forEach(mutableCollection::add);
    }
}
