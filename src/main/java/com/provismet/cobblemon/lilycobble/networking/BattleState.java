package com.provismet.cobblemon.lilycobble.networking;

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record BattleState (List<String> fieldEffects, List<BattleSideState> sides) {
    public static final Codec<BattleState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().optionalFieldOf("field_effects", List.of()).forGetter(BattleState::fieldEffects),
        BattleSideState.CODEC.listOf().optionalFieldOf("sides", List.of()).forGetter(BattleState::sides)
    ).apply(instance, BattleState::new));

    public static final PacketCodec<RegistryByteBuf, BattleState> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.STRING.collect(PacketCodecs.toList()),
        BattleState::fieldEffects,
        BattleSideState.PACKET_CODEC.collect(PacketCodecs.toList()),
        BattleState::sides,
        BattleState::new
    );

    public static BattleState of (PokemonBattle battle) {
        Set<String> fieldEffects = new HashSet<>();
        extractContext(fieldEffects, battle, BattleContext.Type.WEATHER);
        extractContext(fieldEffects, battle, BattleContext.Type.ROOM);
        extractContext(fieldEffects, battle, BattleContext.Type.TERRAIN);

        List<BattleSideState> sides = List.of(BattleSideState.of(battle.getSide1()), BattleSideState.of(battle.getSide2()));

        return new BattleState(fieldEffects.stream().toList(), sides);
    }

    private static void extractContext (Collection<String> mutableCollection, PokemonBattle battle, BattleContext.Type contextType) {
        Collection<BattleContext> fullContext = battle.getContextManager().get(contextType);
        if (fullContext == null) return;

        fullContext.stream()
            .map(BattleContext::getId)
            .forEach(mutableCollection::add);
    }
}
