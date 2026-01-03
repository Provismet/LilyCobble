package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.IntPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.HeldItemPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.StatsPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.StringPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A predicate implementation for testing textual and numerical aspects of a pokemon.
 */
@SuppressWarnings("unused")
public record PokemonPredicate (
    StringPredicate speciesShowdownIds,
    StringPredicate formShowdownIds,
    StringPredicate speciesFormShowdownIds,
    StringPredicate aspects,
    StringPredicate moves,
    IntPredicate level,
    IntPredicate friendship,
    IntPredicate fullness,
    StringPredicate speciesLabels,
    StringPredicate formLabels,
    StatsPredicate EVs,
    StatsPredicate IVs,
    HeldItemPredicate heldItem
) implements Predicate<Pokemon> {
    public static final Codec<PokemonPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        StringPredicate.CODEC.optionalFieldOf("species_showdown_ids", StringPredicate.TRUE).forGetter(PokemonPredicate::speciesShowdownIds),
        StringPredicate.CODEC.optionalFieldOf("form_showdown_ids", StringPredicate.TRUE).forGetter(PokemonPredicate::formShowdownIds),
        StringPredicate.CODEC.optionalFieldOf("speciesform_showdown_ids", StringPredicate.TRUE).forGetter(PokemonPredicate::speciesFormShowdownIds),
        StringPredicate.CODEC.optionalFieldOf("aspects", StringPredicate.TRUE).forGetter(PokemonPredicate::aspects),
        StringPredicate.CODEC.optionalFieldOf("moves", StringPredicate.TRUE).forGetter(PokemonPredicate::moves),
        IntPredicate.CODEC.optionalFieldOf("level", IntPredicate.TRUE).forGetter(PokemonPredicate::level),
        IntPredicate.CODEC.optionalFieldOf("friendship", IntPredicate.TRUE).forGetter(PokemonPredicate::friendship),
        IntPredicate.CODEC.optionalFieldOf("fullness", IntPredicate.TRUE).forGetter(PokemonPredicate::fullness),
        StringPredicate.CODEC.optionalFieldOf("species_labels", StringPredicate.TRUE).forGetter(PokemonPredicate::speciesLabels),
        StringPredicate.CODEC.optionalFieldOf("form_labels", StringPredicate.TRUE).forGetter(PokemonPredicate::formLabels),
        StatsPredicate.CODEC.optionalFieldOf("EVs", StatsPredicate.TRUE).forGetter(PokemonPredicate::EVs),
        StatsPredicate.CODEC.optionalFieldOf("IVs", StatsPredicate.TRUE).forGetter(PokemonPredicate::IVs),
        HeldItemPredicate.CODEC.optionalFieldOf("held_item", HeldItemPredicate.TRUE).forGetter(PokemonPredicate::heldItem)
    ).apply(instance, PokemonPredicate::new));

    public static final PokemonPredicate TRUE = new PokemonPredicate(StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, IntPredicate.TRUE, IntPredicate.TRUE, IntPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, StatsPredicate.TRUE, StatsPredicate.TRUE, HeldItemPredicate.TRUE);
    public static final PokemonPredicate FALSE = new PokemonPredicate(StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, IntPredicate.FALSE, IntPredicate.FALSE, IntPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, StatsPredicate.FALSE, StatsPredicate.FALSE, HeldItemPredicate.FALSE);

    public boolean test (@Nullable Pokemon pokemon) {
        if (pokemon == null) return this.equals(TRUE);

        return this.speciesShowdownIds.test(pokemon.getSpecies().showdownId())
            && this.formShowdownIds.test(pokemon.getForm().formOnlyShowdownId())
            && this.speciesFormShowdownIds.test(pokemon.showdownId())
            && this.aspects.test(pokemon.getAspects())
            && this.moves.test(pokemon.getMoveSet().getMoves().stream().filter(Objects::nonNull).map(move -> move.getName().toLowerCase(Locale.ROOT)).toList())
            && this.level.test(pokemon.getLevel())
            && this.friendship.test(pokemon.getFriendship())
            && this.fullness.test(pokemon.getCurrentFullness())
            && this.speciesLabels.test(pokemon.getSpecies().getLabels())
            && this.formLabels.test(pokemon.getForm().getLabels())
            && this.EVs.test(pokemon.getEvs())
            && this.IVs.test(pokemon.getIvs())
            && this.heldItem.test(pokemon.getHeldItem$common());
    }

    public static class Builder {
        private StringPredicate speciesId = StringPredicate.TRUE;
        private StringPredicate formId = StringPredicate.TRUE;
        private StringPredicate speciesFormId = StringPredicate.TRUE;
        private StringPredicate aspects = StringPredicate.TRUE;
        private StringPredicate moves = StringPredicate.TRUE;
        private IntPredicate level = IntPredicate.TRUE;
        private IntPredicate friendship = IntPredicate.TRUE;
        private IntPredicate fullness = IntPredicate.TRUE;
        private StringPredicate speciesLabels = StringPredicate.TRUE;
        private StringPredicate formLabels = StringPredicate.TRUE;
        private StatsPredicate EVs = StatsPredicate.TRUE;
        private StatsPredicate IVs = StatsPredicate.TRUE;
        private HeldItemPredicate heldItem = HeldItemPredicate.TRUE;

        public Builder species (StringPredicate species) {
            this.speciesId = species;
            return this;
        }

        public Builder species (StringPredicate.Builder builder) {
            this.speciesId = builder.build();
            return this;
        }

        public Builder form (StringPredicate form) {
            this.formId = form;
            return this;
        }

        public Builder form (StringPredicate.Builder builder) {
            this.formId = builder.build();
            return this;
        }

        public Builder speciesForm (StringPredicate speciesForm) {
            this.speciesFormId = speciesForm;
            return this;
        }

        public Builder speciesForm (StringPredicate.Builder builder) {
            this.speciesFormId = builder.build();
            return this;
        }

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

        public Builder speciesLabels (StringPredicate speciesLabels) {
            this.speciesLabels = speciesLabels;
            return this;
        }

        public Builder speciesLabels (StringPredicate.Builder builder) {
            this.speciesLabels = builder.build();
            return this;
        }

        public Builder formLabels (StringPredicate formLabels) {
            this.formLabels = formLabels;
            return this;
        }

        public Builder formLabels (StringPredicate.Builder builder) {
            this.formLabels = builder.build();
            return this;
        }

        public Builder level (IntPredicate matcher) {
            this.level = matcher;
            return this;
        }

        public Builder friendship (IntPredicate matcher) {
            this.friendship = matcher;
            return this;
        }

        public Builder fullness (IntPredicate matcher) {
            this.fullness = matcher;
            return this;
        }

        public Builder EVs (StatsPredicate matcher) {
            this.EVs = matcher;
            return this;
        }

        public Builder EVs (StatsPredicate.Builder matcher) {
            this.EVs = matcher.build();
            return this;
        }

        public Builder IVs (StatsPredicate matcher) {
            this.IVs = matcher;
            return this;
        }

        public Builder IVs (StatsPredicate.Builder matcher) {
            this.IVs = matcher.build();
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

        public PokemonPredicate build () {
            return new PokemonPredicate(
                Objects.requireNonNull(this.speciesId),
                Objects.requireNonNull(this.formId),
                Objects.requireNonNull(this.speciesFormId),
                Objects.requireNonNull(this.aspects),
                Objects.requireNonNull(this.moves),
                Objects.requireNonNull(this.level),
                Objects.requireNonNull(this.friendship),
                Objects.requireNonNull(this.fullness),
                Objects.requireNonNull(this.speciesLabels),
                Objects.requireNonNull(this.formLabels),
                Objects.requireNonNull(this.EVs),
                Objects.requireNonNull(this.IVs),
                Objects.requireNonNull(this.heldItem)
            );
        }
    }
}
