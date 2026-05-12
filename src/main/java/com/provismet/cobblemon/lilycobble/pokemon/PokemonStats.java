package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.api.pokemon.stats.Stat;
import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.EVs;
import com.cobblemon.mod.common.pokemon.IVs;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

import java.util.Optional;

public record PokemonStats (
    Optional<Integer> hp,
    Optional<Integer> attack,
    Optional<Integer> defence,
    Optional<Integer> specialAttack,
    Optional<Integer> specialDefence,
    Optional<Integer> speed
) {
    public static final Codec<PokemonStats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.NONNEGATIVE_INT.optionalFieldOf("hp").forGetter(PokemonStats::hp),
        Codecs.NONNEGATIVE_INT.optionalFieldOf("attack").forGetter(PokemonStats::attack),
        Codecs.NONNEGATIVE_INT.optionalFieldOf("defence").forGetter(PokemonStats::defence),
        Codecs.NONNEGATIVE_INT.optionalFieldOf("special_attack").forGetter(PokemonStats::specialAttack),
        Codecs.NONNEGATIVE_INT.optionalFieldOf("special_defence").forGetter(PokemonStats::specialDefence),
        Codecs.NONNEGATIVE_INT.optionalFieldOf("speed").forGetter(PokemonStats::speed)
    ).apply(instance, PokemonStats::new));

    public static final PokemonStats DEFAULT = new PokemonStats(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    );

    public static PokemonStats fromEVs (EVs evs) {
        return new PokemonStats(
            Optional.ofNullable(evs.get(Stats.HP)),
            Optional.ofNullable(evs.get(Stats.ATTACK)),
            Optional.ofNullable(evs.get(Stats.DEFENCE)),
            Optional.ofNullable(evs.get(Stats.SPECIAL_ATTACK)),
            Optional.ofNullable(evs.get(Stats.SPECIAL_DEFENCE)),
            Optional.ofNullable(evs.get(Stats.SPEED))
        );
    }

    public static PokemonStats fromIVs (IVs ivs) {
        return new PokemonStats(
            Optional.ofNullable(ivs.get(Stats.HP)),
            Optional.ofNullable(ivs.get(Stats.ATTACK)),
            Optional.ofNullable(ivs.get(Stats.DEFENCE)),
            Optional.ofNullable(ivs.get(Stats.SPECIAL_ATTACK)),
            Optional.ofNullable(ivs.get(Stats.SPECIAL_DEFENCE)),
            Optional.ofNullable(ivs.get(Stats.SPEED))
        );
    }

    public Optional<Integer> getStat (Stat stat) {
        return switch (stat) {
            case Stats.HP -> this.hp;
            case Stats.ATTACK -> this.attack;
            case Stats.DEFENCE -> this.defence;
            case Stats.SPECIAL_ATTACK -> this.specialAttack;
            case Stats.SPECIAL_DEFENCE -> this.specialDefence;
            case Stats.SPEED -> this.speed;
            default -> Optional.empty();
        };
    }

    public PokemonStats withStat (Stat stat, int value) {
        return switch (stat) {
            case Stats.HP -> this.withHP(value);
            case Stats.ATTACK -> this.withAttack(value);
            case Stats.DEFENCE -> this.withDefence(value);
            case Stats.SPECIAL_ATTACK -> this.withSpecialAttack(value);
            case Stats.SPECIAL_DEFENCE -> this.withSpecialDefence(value);
            case Stats.SPEED -> this.withSpeed(value);
            case null, default -> this;
        };
    }

    public PokemonStats withoutStat (Stat stat) {
        return switch (stat) {
            case Stats.HP -> this.withoutHP();
            case Stats.ATTACK -> this.withoutAttack();
            case Stats.DEFENCE -> this.withoutDefence();
            case Stats.SPECIAL_ATTACK -> this.withoutSpecialAttack();
            case Stats.SPECIAL_DEFENCE -> this.withoutSpecialDefence();
            case Stats.SPEED -> this.withoutSpeed();
            case null, default -> this;
        };
    }

    public PokemonStats withHP (int value) {
        return new PokemonStats(
            Optional.of(value),
            this.attack,
            this.defence,
            this.specialAttack,
            this.specialDefence,
            this.speed
        );
    }

    public PokemonStats withoutHP () {
        return new PokemonStats(
            Optional.empty(),
            this.attack,
            this.defence,
            this.specialAttack,
            this.specialDefence,
            this.speed
        );
    }

    public PokemonStats withAttack (int value) {
        return new PokemonStats(
            this.hp,
            Optional.of(value),
            this.defence,
            this.specialAttack,
            this.specialDefence,
            this.speed
        );
    }

    public PokemonStats withoutAttack () {
        return new PokemonStats(
            this.hp,
            Optional.empty(),
            this.defence,
            this.specialAttack,
            this.specialDefence,
            this.speed
        );
    }

    public PokemonStats withDefence (int value) {
        return new PokemonStats(
            this.hp,
            this.attack,
            Optional.of(value),
            this.specialAttack,
            this.specialDefence,
            this.speed
        );
    }

    public PokemonStats withoutDefence () {
        return new PokemonStats(
            this.hp,
            this.attack,
            Optional.empty(),
            this.specialAttack,
            this.specialDefence,
            this.speed
        );
    }

    public PokemonStats withSpecialAttack (int value) {
        return new PokemonStats(
            this.hp,
            this.attack,
            this.defence,
            Optional.of(value),
            this.specialDefence,
            this.speed
        );
    }

    public PokemonStats withoutSpecialAttack () {
        return new PokemonStats(
            this.hp,
            this.attack,
            this.defence,
            Optional.empty(),
            this.specialDefence,
            this.speed
        );
    }

    public PokemonStats withSpecialDefence (int value) {
        return new PokemonStats(
            this.hp,
            this.attack,
            this.defence,
            this.specialAttack,
            Optional.of(value),
            this.speed
        );
    }

    public PokemonStats withoutSpecialDefence () {
        return new PokemonStats(
            this.hp,
            this.attack,
            this.defence,
            this.specialAttack,
            Optional.empty(),
            this.speed
        );
    }

    public PokemonStats withSpeed (int value) {
        return new PokemonStats(
            this.hp,
            this.attack,
            this.defence,
            this.specialAttack,
            this.specialDefence,
            Optional.of(value)
        );
    }

    public PokemonStats withoutSpeed () {
        return new PokemonStats(
            this.hp,
            this.attack,
            this.defence,
            this.specialAttack,
            this.specialDefence,
            Optional.empty()
        );
    }

    public void applyEVs (Pokemon pokemon) {
        this.hp.ifPresent(integer -> pokemon.getEvs().set(Stats.HP, integer));
        this.attack.ifPresent(integer -> pokemon.getEvs().set(Stats.ATTACK, integer));
        this.defence.ifPresent(integer -> pokemon.getEvs().set(Stats.DEFENCE, integer));
        this.specialAttack.ifPresent(integer -> pokemon.getEvs().set(Stats.SPECIAL_ATTACK, integer));
        this.specialDefence.ifPresent(integer -> pokemon.getEvs().set(Stats.SPECIAL_DEFENCE, integer));
        this.speed.ifPresent(integer -> pokemon.getEvs().set(Stats.SPEED, integer));
        pokemon.onChange(null);
    }

    public void applyIVs (Pokemon pokemon) {
        this.hp.ifPresent(integer -> pokemon.getIvs().set(Stats.HP, integer));
        this.attack.ifPresent(integer -> pokemon.getIvs().set(Stats.ATTACK, integer));
        this.defence.ifPresent(integer -> pokemon.getIvs().set(Stats.DEFENCE, integer));
        this.specialAttack.ifPresent(integer -> pokemon.getIvs().set(Stats.SPECIAL_ATTACK, integer));
        this.specialDefence.ifPresent(integer -> pokemon.getIvs().set(Stats.SPECIAL_DEFENCE, integer));
        this.speed.ifPresent(integer -> pokemon.getIvs().set(Stats.SPEED, integer));
        pokemon.onChange(null);
    }

    public IVs toIVs () {
        IVs ivs = new IVs();
        this.hp.ifPresent(integer -> ivs.set(Stats.HP, integer));
        this.attack.ifPresent(integer -> ivs.set(Stats.ATTACK, integer));
        this.defence.ifPresent(integer -> ivs.set(Stats.DEFENCE, integer));
        this.specialAttack.ifPresent(integer -> ivs.set(Stats.SPECIAL_ATTACK, integer));
        this.specialDefence.ifPresent(integer -> ivs.set(Stats.SPECIAL_DEFENCE, integer));
        this.speed.ifPresent(integer -> ivs.set(Stats.SPEED, integer));
        return ivs;
    }

    public EVs toEVs () {
        EVs evs = new EVs();
        this.hp.ifPresent(integer -> evs.set(Stats.HP, integer));
        this.attack.ifPresent(integer -> evs.set(Stats.ATTACK, integer));
        this.defence.ifPresent(integer -> evs.set(Stats.DEFENCE, integer));
        this.specialAttack.ifPresent(integer -> evs.set(Stats.SPECIAL_ATTACK, integer));
        this.specialDefence.ifPresent(integer -> evs.set(Stats.SPECIAL_DEFENCE, integer));
        this.speed.ifPresent(integer -> evs.set(Stats.SPEED, integer));
        return evs;
    }
}