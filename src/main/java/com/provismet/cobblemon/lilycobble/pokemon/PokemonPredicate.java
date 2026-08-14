package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.IntPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.HeldItemPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.StatsPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.StringPredicate;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A predicate implementation for testing textual and numerical aspects of a pokemon.
 */
@SuppressWarnings("unused")
public record PokemonPredicate (
    PokemonSpeciesPredicate species,
    PokemonInstancePredicate instance
) implements Predicate<Pokemon> {
    private static final Codec<PokemonPredicate> CODEC_NEW = RecordCodecBuilder.create(instance -> instance.group(
        PokemonSpeciesPredicate.CODEC.optionalFieldOf("species", PokemonSpeciesPredicate.TRUE).forGetter(PokemonPredicate::species),
        PokemonInstancePredicate.CODEC.optionalFieldOf("instance", PokemonInstancePredicate.TRUE).forGetter(PokemonPredicate::instance)
    ).apply(instance, PokemonPredicate::new));

    private static final Codec<PokemonPredicate> CODEC_OLD = RecordCodecBuilder.create(instance -> instance.group(
        StringPredicate.CODEC.optionalFieldOf("species_showdown_ids", StringPredicate.TRUE).forGetter(p -> p.species.speciesShowdownIds()),
        StringPredicate.CODEC.optionalFieldOf("form_showdown_ids", StringPredicate.TRUE).forGetter(p -> p.species.formShowdownIds()),
        StringPredicate.CODEC.optionalFieldOf("speciesform_showdown_ids", StringPredicate.TRUE).forGetter(p -> p.species.speciesFormShowdownIds()),
        StringPredicate.CODEC.optionalFieldOf("aspects", StringPredicate.TRUE).forGetter(p -> p.instance.aspects()),
        StringPredicate.CODEC.optionalFieldOf("moves", StringPredicate.TRUE).forGetter(p -> p.instance.moves()),
        StringPredicate.CODEC.optionalFieldOf("ability", StringPredicate.TRUE).forGetter(p -> p.instance.ability()),
        IntPredicate.CODEC.optionalFieldOf("level", IntPredicate.TRUE).forGetter(p -> p.instance.level()),
        IntPredicate.CODEC.optionalFieldOf("friendship", IntPredicate.TRUE).forGetter(p -> p.instance.friendship()),
        IntPredicate.CODEC.optionalFieldOf("fullness", IntPredicate.TRUE).forGetter(p -> p.instance.fullness()),
        StringPredicate.CODEC.optionalFieldOf("species_labels", StringPredicate.TRUE).forGetter(p -> p.species.speciesLabels()),
        StringPredicate.CODEC.optionalFieldOf("form_labels", StringPredicate.TRUE).forGetter(p -> p.species.formLabels()),
        StatsPredicate.CODEC.optionalFieldOf("EVs", StatsPredicate.TRUE).forGetter(p -> p.instance.EVs()),
        StatsPredicate.CODEC.optionalFieldOf("IVs", StatsPredicate.TRUE).forGetter(p -> p.instance.IVs()),
        HeldItemPredicate.CODEC.optionalFieldOf("held_item", HeldItemPredicate.TRUE).forGetter(p -> p.instance.heldItem()),
        Codec.BOOL.optionalFieldOf("has_evolution").forGetter(p -> p.species.hasEvolution()),
        Codec.BOOL.optionalFieldOf("has_pre-evolution").forGetter(p -> p.species.hasPreEvolution())
    ).apply(instance, (speciesId, formId, speciesFormId, aspects, moves, ability, level, friendship, fullness, speciesLabel, formLabel, ev, iv, held, hasEvo, hasPreEvo) ->
        new PokemonPredicate(
            new PokemonSpeciesPredicate(speciesId, formId, speciesFormId, speciesLabel, formLabel, StringPredicate.TRUE, StatsPredicate.TRUE, hasEvo, hasPreEvo),
            new PokemonInstancePredicate(aspects, moves, ability, StringPredicate.TRUE, level, friendship, fullness, ev, iv, held))
    ));

    public static final Codec<PokemonPredicate> CODEC = Codec.withAlternative(CODEC_NEW, CODEC_OLD);

    public static final PokemonPredicate TRUE = new PokemonPredicate(PokemonSpeciesPredicate.TRUE, PokemonInstancePredicate.TRUE);
    public static final PokemonPredicate FALSE = new PokemonPredicate(PokemonSpeciesPredicate.FALSE, PokemonInstancePredicate.FALSE);

    public static Builder builder () {
        return new Builder();
    }

    public boolean test (@Nullable Pokemon pokemon) {
        if (pokemon == null) return this.equals(TRUE);

        return this.species.test(pokemon)
            && this.instance.test(pokemon);
    }

    @Override
    public boolean equals (Object object) {
        if (!(object instanceof PokemonPredicate other)) return false;

        return Objects.equals(this.species, other.species)
            && Objects.equals(this.instance, other.instance);
    }

    @Override
    public int hashCode () {
        return Objects.hash(this.species, this.instance);
    }

    public static class Builder implements Supplier<PokemonPredicate> {
        PokemonSpeciesPredicate species = PokemonSpeciesPredicate.TRUE;
        PokemonInstancePredicate instance = PokemonInstancePredicate.TRUE;

        public Builder species (PokemonSpeciesPredicate species) {
            this.species = species;
            return this;
        }

        public Builder species (PokemonSpeciesPredicate.Builder builder) {
            this.species = builder.build();
            return this;
        }

        public Builder instance (PokemonInstancePredicate instance) {
            this.instance = instance;
            return this;
        }

        public Builder instance (PokemonInstancePredicate.Builder builder) {
            this.instance = builder.build();
            return this;
        }

        @Override
        public PokemonPredicate get () {
            return new PokemonPredicate(
                Objects.requireNonNull(this.species),
                Objects.requireNonNull(this.instance)
            );
        }
    }
}
