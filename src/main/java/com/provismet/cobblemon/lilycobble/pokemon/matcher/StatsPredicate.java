package com.provismet.cobblemon.lilycobble.pokemon.matcher;

import com.cobblemon.mod.common.api.pokemon.stats.Stats;
import com.cobblemon.mod.common.pokemon.PokemonStats;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public record StatsPredicate (
    IntPredicate health,
    IntPredicate attack,
    IntPredicate defence,
    IntPredicate specialAttack,
    IntPredicate specialDefence,
    IntPredicate speed
) implements Predicate<PokemonStats> {
    public static final Codec<StatsPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        IntPredicate.CODEC.optionalFieldOf("health", IntPredicate.TRUE).forGetter(StatsPredicate::health),
        IntPredicate.CODEC.optionalFieldOf("attack", IntPredicate.TRUE).forGetter(StatsPredicate::attack),
        IntPredicate.CODEC.optionalFieldOf("defence", IntPredicate.TRUE).forGetter(StatsPredicate::defence),
        IntPredicate.CODEC.optionalFieldOf("special_attack", IntPredicate.TRUE).forGetter(StatsPredicate::specialAttack),
        IntPredicate.CODEC.optionalFieldOf("special_defence", IntPredicate.TRUE).forGetter(StatsPredicate::specialDefence),
        IntPredicate.CODEC.optionalFieldOf("speed", IntPredicate.TRUE).forGetter(StatsPredicate::speed)
    ).apply(instance, StatsPredicate::new));

    public static final StatsPredicate TRUE = new StatsPredicate(IntPredicate.TRUE, IntPredicate.TRUE, IntPredicate.TRUE, IntPredicate.TRUE, IntPredicate.TRUE, IntPredicate.TRUE);
    public static final StatsPredicate FALSE = new StatsPredicate(IntPredicate.FALSE, IntPredicate.FALSE, IntPredicate.FALSE, IntPredicate.FALSE, IntPredicate.FALSE, IntPredicate.FALSE);

    public static Builder builder () {
        return new Builder();
    }

    public boolean test (PokemonStats stats) {
        if (stats == null) return this.equals(TRUE);

        return this.health.test(stats.getOrDefault(Stats.HP))
            && this.attack.test(stats.getOrDefault(Stats.ATTACK))
            && this.defence.test(stats.getOrDefault(Stats.DEFENCE))
            && this.specialAttack.test(stats.getOrDefault(Stats.SPECIAL_ATTACK))
            && this.specialDefence.test(stats.getOrDefault(Stats.SPECIAL_DEFENCE))
            && this.speed.test(stats.getOrDefault(Stats.SPEED));
    }

    public static class Builder {
        private IntPredicate health = IntPredicate.TRUE;
        private IntPredicate attack = IntPredicate.TRUE;
        private IntPredicate defence = IntPredicate.TRUE;
        private IntPredicate specialAttack = IntPredicate.TRUE;
        private IntPredicate specialDefence = IntPredicate.TRUE;
        private IntPredicate speed = IntPredicate.TRUE;

        public Builder health (IntPredicate predicate) {
            this.health = predicate;
            return this;
        }

        public Builder attack (IntPredicate predicate) {
            this.attack = predicate;
            return this;
        }

        public Builder defence (IntPredicate predicate) {
            this.defence = predicate;
            return this;
        }

        public Builder specialAttack (IntPredicate predicate) {
            this.specialAttack = predicate;
            return this;
        }

        public Builder specialDefence (IntPredicate predicate) {
            this.specialDefence = predicate;
            return this;
        }

        public Builder speed (IntPredicate predicate) {
            this.speed = predicate;
            return this;
        }

        public StatsPredicate build () {
            return new StatsPredicate(
                Objects.requireNonNull(this.health),
                Objects.requireNonNull(this.attack),
                Objects.requireNonNull(this.defence),
                Objects.requireNonNull(this.specialAttack),
                Objects.requireNonNull(this.specialDefence),
                Objects.requireNonNull(this.speed)
            );
        }
    }
}
