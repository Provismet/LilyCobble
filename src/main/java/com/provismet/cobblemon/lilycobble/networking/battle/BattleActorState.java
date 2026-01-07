package com.provismet.cobblemon.lilycobble.networking.battle;

import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.entity.npc.NPCEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record BattleActorState (UUID uuid, List<BattlePokemonState> team) {
    public static final Codec<BattleActorState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Uuids.CODEC.fieldOf("uuid").forGetter(BattleActorState::uuid),
        BattlePokemonState.CODEC.listOf().optionalFieldOf("team", List.of()).forGetter(BattleActorState::team)
    ).apply(instance, BattleActorState::new));

    public static final PacketCodec<RegistryByteBuf, BattleActorState> PACKET_CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC,
        BattleActorState::uuid,
        BattlePokemonState.PACKET_CODEC.collect(PacketCodecs.toList()),
        BattleActorState::team,
        BattleActorState::new
    );

    public static BattleActorState of (BattleActor actor) {
        List<BattlePokemonState> pokemon = actor.getPokemonList()
            .stream()
            .map(BattlePokemonState::of)
            .toList();

        return new BattleActorState(actor.getUuid(), pokemon);
    }

    public boolean isFor (PlayerEntity player) {
        return this.uuid.equals(player.getUuid());
    }

    public boolean isFor (NPCEntity npc) {
        return this.uuid.equals(npc.getUuid());
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof BattleActorState(UUID otherUUID, List<BattlePokemonState> otherTeam))) return false;
        return Objects.equals(this.uuid, otherUUID) && Objects.equals(this.team, otherTeam);
    }

    @Override
    public int hashCode () {
        return Objects.hash(this.uuid, this.team);
    }
}
