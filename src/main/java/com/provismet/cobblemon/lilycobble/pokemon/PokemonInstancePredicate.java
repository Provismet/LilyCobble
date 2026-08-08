package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.HeldItemPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.IntPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.StatsPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.StringPredicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A predicate used to test properties of a Pokemon that are specific to that individual.
 */
@SuppressWarnings("unused")
public record PokemonInstancePredicate (
    StringPredicate aspects,
    StringPredicate moves,
    StringPredicate ability,
    StringPredicate marks,
    IntPredicate level,
    IntPredicate friendship,
    IntPredicate fullness,
    StatsPredicate EVs,
    StatsPredicate IVs,
    HeldItemPredicate heldItem
) implements Predicate<Pokemon> {
    public static final Codec<PokemonInstancePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        StringPredicate.CODEC.optionalFieldOf("aspects", StringPredicate.TRUE).forGetter(PokemonInstancePredicate::aspects),
        StringPredicate.CODEC.optionalFieldOf("moves", StringPredicate.TRUE).forGetter(PokemonInstancePredicate::moves),
        StringPredicate.CODEC.optionalFieldOf("ability", StringPredicate.TRUE).forGetter(PokemonInstancePredicate::ability),
        StringPredicate.CODEC.optionalFieldOf("marks", StringPredicate.TRUE).forGetter(PokemonInstancePredicate::marks),
        IntPredicate.CODEC.optionalFieldOf("level", IntPredicate.TRUE).forGetter(PokemonInstancePredicate::level),
        IntPredicate.CODEC.optionalFieldOf("friendship", IntPredicate.TRUE).forGetter(PokemonInstancePredicate::friendship),
        IntPredicate.CODEC.optionalFieldOf("fullness", IntPredicate.TRUE).forGetter(PokemonInstancePredicate::fullness),
        StatsPredicate.CODEC.optionalFieldOf("EVs", StatsPredicate.TRUE).forGetter(PokemonInstancePredicate::EVs),
        StatsPredicate.CODEC.optionalFieldOf("IVs", StatsPredicate.TRUE).forGetter(PokemonInstancePredicate::IVs),
        HeldItemPredicate.CODEC.optionalFieldOf("held_item", HeldItemPredicate.TRUE).forGetter(PokemonInstancePredicate::heldItem)
    ).apply(instance, PokemonInstancePredicate::new));

    public static final PokemonInstancePredicate TRUE = new PokemonInstancePredicate(StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, IntPredicate.TRUE, IntPredicate.TRUE, IntPredicate.TRUE, StatsPredicate.TRUE, StatsPredicate.TRUE, HeldItemPredicate.TRUE);
    public static final PokemonInstancePredicate FALSE = new PokemonInstancePredicate(StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, IntPredicate.FALSE, IntPredicate.FALSE, IntPredicate.FALSE, StatsPredicate.FALSE, StatsPredicate.FALSE, HeldItemPredicate.FALSE);

    @Override
    public boolean test (Pokemon pokemon) {
        if (pokemon == null) return this.equals(TRUE);

        return this.aspects.test(pokemon.getAspects())
            && this.moves.test(pokemon.getMoveSet().getMoves().stream().filter(Objects::nonNull).map(move -> move.getName().toLowerCase(Locale.ROOT)).toList())
            && this.ability.test(pokemon.getAbility().getTemplate().getName())
            && this.marks.test(pokemon.getMarks().stream().map(mark -> mark.getIdentifier().toString()).toList())
            && this.level.test(pokemon.getLevel())
            && this.friendship.test(pokemon.getFriendship())
            && this.fullness.test(pokemon.getCurrentFullness())
            && this.EVs.test(pokemon.getEvs())
            && this.IVs.test(pokemon.getIvs())
            && this.heldItem.test(pokemon.getHeldItem$common());
    }

    @Override
    public boolean equals (Object object) {
        if (!(object instanceof PokemonInstancePredicate other)) return false;

        return Objects.equals(this.level, other.level)
            && Objects.equals(this.EVs, other.EVs)
            && Objects.equals(this.IVs, other.IVs)
            && Objects.equals(this.moves, other.moves)
            && Objects.equals(this.marks, other.marks)
            && Objects.equals(this.fullness, other.fullness)
            && Objects.equals(this.aspects, other.aspects)
            && Objects.equals(this.ability, other.ability)
            && Objects.equals(this.friendship, other.friendship)
            && Objects.equals(this.heldItem, other.heldItem);
    }

    @Override
    public int hashCode () {
        return Objects.hash(
            this.aspects,
            this.moves,
            this.ability,
            this.marks,
            this.level,
            this.friendship,
            this.fullness,
            this.EVs,
            this.IVs,
            this.heldItem
        );
    }

    public static class Builder implements Supplier<PokemonInstancePredicate> {
        private StringPredicate aspects = StringPredicate.TRUE;
        private StringPredicate moves = StringPredicate.TRUE;
        private StringPredicate ability = StringPredicate.TRUE;
        private StringPredicate marks = StringPredicate.TRUE;
        private IntPredicate level = IntPredicate.TRUE;
        private IntPredicate friendship = IntPredicate.TRUE;
        private IntPredicate fullness = IntPredicate.TRUE;
        private StatsPredicate EVs = StatsPredicate.TRUE;
        private StatsPredicate IVs = StatsPredicate.TRUE;
        private HeldItemPredicate heldItem = HeldItemPredicate.TRUE;

        public Builder aspects (StringPredicate aspects) {
            this.aspects = aspects;
            return this;
        }

        public Builder aspects (StringPredicate.Builder builder) {
            this.aspects = builder.build();
            return this;
        }

        public Builder moves (StringPredicate moves) {
            this.moves = moves;
            return this;
        }

        public Builder moves (StringPredicate.Builder builder) {
            this.moves = builder.build();
            return this;
        }

        public Builder ability (StringPredicate ability) {
            this.ability = ability;
            return this;
        }

        public Builder ability (StringPredicate.Builder ability) {
            this.ability = ability.build();
            return this;
        }

        public Builder ability (String ability) {
            this.ability = StringPredicate.builder().whitelist(ability).build();
            return this;
        }

        public Builder marks (StringPredicate marks) {
            this.marks = marks;
            return this;
        }

        public Builder marks (StringPredicate.Builder builder) {
            this.marks = builder.build();
            return this;
        }

        public Builder marks (String mark) {
            this.marks = StringPredicate.builder().whitelist(mark).build();
            return this;
        }

        public Builder marks (Identifier markId) {
            this.marks = StringPredicate.builder().whitelist(markId.toString()).build();
            return this;
        }

        public Builder level (IntPredicate level) {
            this.level = level;
            return this;
        }

        public Builder friendship (IntPredicate friendship) {
            this.friendship = friendship;
            return this;
        }

        public Builder fullness (IntPredicate fullness) {
            this.fullness = fullness;
            return this;
        }

        public Builder EVs (StatsPredicate evs) {
            this.EVs = evs;
            return this;
        }

        public Builder EVs (StatsPredicate.Builder builder) {
            this.EVs = builder.build();
            return this;
        }

        public Builder IVs (StatsPredicate ivs) {
            this.IVs = ivs;
            return this;
        }

        public Builder IVs (StatsPredicate.Builder builder) {
            this.IVs = builder.build();
            return this;
        }

        public Builder heldItem (HeldItemPredicate heldItem) {
            this.heldItem = heldItem;
            return this;
        }

        public Builder heldItem (HeldItemPredicate.Builder builder) {
            this.heldItem = builder.build();
            return this;
        }

        public Builder heldItem (String showdownId) {
            this.heldItem = HeldItemPredicate.builder().whitelist(showdownId).build();
            return this;
        }

        public Builder heldItem (Item item) {
            this.heldItem = HeldItemPredicate.builder().whitelist(item.getDefaultStack()).build();
            return this;
        }

        public Builder heldItem (ItemStack item) {
            this.heldItem = HeldItemPredicate.builder().whitelist(item).build();
            return this;
        }

        public PokemonInstancePredicate build () {
            return this.get();
        }

        @Override
        public PokemonInstancePredicate get() {
            return new PokemonInstancePredicate(
                Objects.requireNonNull(this.aspects),
                Objects.requireNonNull(this.moves),
                Objects.requireNonNull(this.ability),
                Objects.requireNonNull(this.marks),
                Objects.requireNonNull(this.level),
                Objects.requireNonNull(this.friendship),
                Objects.requireNonNull(this.fullness),
                Objects.requireNonNull(this.EVs),
                Objects.requireNonNull(this.IVs),
                Objects.requireNonNull(this.heldItem)
            );
        }
    }
}
