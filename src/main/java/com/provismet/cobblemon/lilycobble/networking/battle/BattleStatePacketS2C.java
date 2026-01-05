package com.provismet.cobblemon.lilycobble.networking.battle;

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.lilycobble.LilyCobbleMain;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public record BattleStatePacketS2C(List<String> fieldEffects, List<BattleSideState> sides) implements CustomPayload {
    public static final Id<BattleStatePacketS2C> ID = new Id<>(LilyCobbleMain.identifier("battle_state"));

    public static final Codec<BattleStatePacketS2C> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().optionalFieldOf("field_effects", List.of()).forGetter(BattleStatePacketS2C::fieldEffects),
        BattleSideState.CODEC.listOf().optionalFieldOf("sides", List.of()).forGetter(BattleStatePacketS2C::sides)
    ).apply(instance, BattleStatePacketS2C::new));

    public static final PacketCodec<RegistryByteBuf, BattleStatePacketS2C> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.STRING.collect(PacketCodecs.toList()),
        BattleStatePacketS2C::fieldEffects,
        BattleSideState.PACKET_CODEC.collect(PacketCodecs.toList()),
        BattleStatePacketS2C::sides,
        BattleStatePacketS2C::new
    );

    public static BattleStatePacketS2C of (PokemonBattle battle) {
        Set<String> fieldEffects = new HashSet<>();
        extractContext(fieldEffects, battle, BattleContext.Type.WEATHER);
        extractContext(fieldEffects, battle, BattleContext.Type.ROOM);
        extractContext(fieldEffects, battle, BattleContext.Type.TERRAIN);

        List<BattleSideState> sides = List.of(BattleSideState.of(battle.getSide1()), BattleSideState.of(battle.getSide2()));

        return new BattleStatePacketS2C(fieldEffects.stream().toList(), sides);
    }

    public Optional<BattleSideState> getSide1 () {
        if (this.sides.isEmpty()) return Optional.empty();
        return Optional.of(this.sides.getFirst());
    }

    public Optional<BattleSideState> getSide2 () {
        if (this.sides.size() < 2) return Optional.empty();
        return Optional.of(this.sides.get(1));
    }

    private static void extractContext (Collection<String> mutableCollection, PokemonBattle battle, BattleContext.Type contextType) {
        Collection<BattleContext> fullContext = battle.getContextManager().get(contextType);
        if (fullContext == null) return;

        fullContext.stream()
            .map(BattleContext::getId)
            .forEach(mutableCollection::add);
    }

    @Override
    public Id<? extends CustomPayload> getId () {
        return ID;
    }
}
