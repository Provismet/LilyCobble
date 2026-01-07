package com.provismet.cobblemon.lilycobble.networking.battle;

import com.cobblemon.mod.common.api.battles.interpreter.BattleContext;
import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.cobblemon.mod.common.battles.BattleSide;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.lilycobble.LilyCobbleMain;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
public record BattleStatePacketS2C (List<String> fieldEffects, List<BattleSideState> sides) implements CustomPayload {
    public static final Id<BattleStatePacketS2C> ID = new Id<>(LilyCobbleMain.identifier("battle_state"));
    public static final BattleStatePacketS2C NULL_PACKET = new BattleStatePacketS2C(List.of(), List.of());

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

    public BattleStatePacketS2C (Set<String> fieldEffects, List<BattleSideState> sides) {
        this(fieldEffects.stream().toList(), sides);
    }

    public BattleStatePacketS2C (Collection<String> fieldEffects, BattleSideState... sides) {
        this(fieldEffects.stream().toList(), Arrays.stream(sides).toList());
    }

    public static BattleStatePacketS2C of (BattleSide... sides) {
        return of(Arrays.stream(sides).toList());
    }

    public static BattleStatePacketS2C of (List<BattleSide> sides) {
        if (sides.isEmpty()) return NULL_PACKET;

        Set<String> fieldEffects = getFieldEffects(sides.getFirst().getBattle());
        List<BattleSideState> sideStates = sides.stream().map(BattleSideState::of).toList();
        return new BattleStatePacketS2C(fieldEffects, sideStates);
    }

    public static BattleStatePacketS2C of (PokemonBattle battle) {
        return new BattleStatePacketS2C(
            getFieldEffects(battle),
            BattleSideState.of(battle.getSide1()),
            BattleSideState.of(battle.getSide2())
        );
    }

    public static Set<String> getFieldEffects (PokemonBattle battle) {
        Set<String> fieldEffects = new HashSet<>();
        extractContext(fieldEffects, battle, BattleContext.Type.WEATHER);
        extractContext(fieldEffects, battle, BattleContext.Type.ROOM);
        extractContext(fieldEffects, battle, BattleContext.Type.TERRAIN);
        return fieldEffects;
    }

    public Optional<BattleSideState> getSide1 () {
        if (this.sides.isEmpty()) return Optional.empty();
        return Optional.of(this.sides.getFirst());
    }

    public Optional<BattleSideState> getSide2 () {
        if (this.sides.size() < 2) return Optional.empty();
        return Optional.of(this.sides.get(1));
    }

    public void sendTo (ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, this);
    }

    public CustomPayloadS2CPacket toPacket () {
        return new CustomPayloadS2CPacket(this);
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

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof BattleStatePacketS2C(List<String> otherEffects, List<BattleSideState> otherSides))) return false;
        return Objects.equals(this.fieldEffects, otherEffects) && Objects.equals(this.sides, otherSides);
    }

    @Override
    public int hashCode () {
        return Objects.hash(fieldEffects, sides);
    }
}
