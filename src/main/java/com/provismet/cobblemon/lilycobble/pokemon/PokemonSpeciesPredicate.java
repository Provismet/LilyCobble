package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.StatsPredicate;
import com.provismet.cobblemon.lilycobble.pokemon.matcher.StringPredicate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

/**
 * A predicate used to test properties related to a Pokemon's species/form.
 */
@SuppressWarnings("unused")
public record PokemonSpeciesPredicate(
    StringPredicate speciesShowdownIds,
    StringPredicate formShowdownIds,
    StringPredicate speciesFormShowdownIds,
    StringPredicate speciesLabels,
    StringPredicate formLabels,
    StringPredicate type,
    StatsPredicate baseStats,
    Optional<Boolean> hasEvolution,
    Optional<Boolean> hasPreEvolution
) implements Predicate<Pokemon> {
    public static final Codec<PokemonSpeciesPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        StringPredicate.CODEC.optionalFieldOf("species_showdown_ids", StringPredicate.TRUE).forGetter(PokemonSpeciesPredicate::speciesShowdownIds),
        StringPredicate.CODEC.optionalFieldOf("form_showdown_ids", StringPredicate.TRUE).forGetter(PokemonSpeciesPredicate::formShowdownIds),
        StringPredicate.CODEC.optionalFieldOf("speciesform_showdown_ids", StringPredicate.TRUE).forGetter(PokemonSpeciesPredicate::speciesFormShowdownIds),
        StringPredicate.CODEC.optionalFieldOf("species_labels", StringPredicate.TRUE).forGetter(PokemonSpeciesPredicate::speciesLabels),
        StringPredicate.CODEC.optionalFieldOf("form_labels", StringPredicate.TRUE).forGetter(PokemonSpeciesPredicate::formLabels),
        StringPredicate.CODEC.optionalFieldOf("type", StringPredicate.TRUE).forGetter(PokemonSpeciesPredicate::type),
        StatsPredicate.CODEC.optionalFieldOf("base_stats", StatsPredicate.TRUE).forGetter(PokemonSpeciesPredicate::baseStats),
        Codec.BOOL.optionalFieldOf("has_evolution").forGetter(PokemonSpeciesPredicate::hasEvolution),
        Codec.BOOL.optionalFieldOf("has_pre-evolution").forGetter(PokemonSpeciesPredicate::hasPreEvolution)
    ).apply(instance, PokemonSpeciesPredicate::new));

    public static final PokemonSpeciesPredicate TRUE = new PokemonSpeciesPredicate(StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, StringPredicate.TRUE, StatsPredicate.TRUE, Optional.empty(), Optional.empty());
    public static final PokemonSpeciesPredicate FALSE = new PokemonSpeciesPredicate(StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, StringPredicate.FALSE, StatsPredicate.FALSE, Optional.empty(), Optional.empty());

    public static Builder builder () {
        return new Builder();
    }

    @Override
    public boolean test (Pokemon pokemon) {
        if (pokemon == null) return this.equals(TRUE);

        List<String> types = StreamSupport.stream(pokemon.getTypes().spliterator(), false)
            .map(ElementalType::showdownId)
            .toList();

        return this.speciesShowdownIds.test(pokemon.getSpecies().showdownId())
            && this.formShowdownIds.test(pokemon.getForm().formOnlyShowdownId())
            && this.speciesFormShowdownIds.test(pokemon.showdownId())
            && this.speciesLabels.test(pokemon.getSpecies().getLabels())
            && this.formLabels.test(pokemon.getForm().getLabels())
            && this.type.test(types)
            && this.baseStats.test(pokemon.getForm().getBaseStats())
            && (this.hasEvolution.isEmpty() || this.hasEvolution.get() == !pokemon.getForm().getEvolutions().isEmpty())
            && (this.hasPreEvolution.isEmpty() || this.hasPreEvolution.get() == (pokemon.getPreEvolution() != null));
    }

    @Override
    public boolean equals (Object object) {
        if (!(object instanceof PokemonSpeciesPredicate other)) return false;

        return Objects.equals(this.speciesShowdownIds, other.speciesShowdownIds)
            && Objects.equals(this.formShowdownIds, other.formShowdownIds)
            && Objects.equals(this.speciesFormShowdownIds, other.speciesFormShowdownIds)
            && Objects.equals(this.speciesLabels, other.speciesLabels)
            && Objects.equals(this.formLabels, other.formLabels)
            && Objects.equals(this.hasEvolution, other.hasEvolution)
            && Objects.equals(this.baseStats, other.baseStats)
            && Objects.equals(this.type, other.type)
            && Objects.equals(this.hasPreEvolution, other.hasPreEvolution);
    }

    @Override
    public int hashCode () {
        return Objects.hash(
            this.speciesShowdownIds,
            this.formShowdownIds,
            this.speciesFormShowdownIds,
            this.speciesLabels,
            this.formLabels,
            this.type,
            this.baseStats,
            this.hasEvolution,
            this.hasPreEvolution
        );
    }

    public static class Builder implements Supplier<PokemonSpeciesPredicate> {
        private StringPredicate speciesId = StringPredicate.TRUE;
        private StringPredicate formId = StringPredicate.TRUE;
        private StringPredicate speciesFormId = StringPredicate.TRUE;
        private StringPredicate speciesLabels = StringPredicate.TRUE;
        private StringPredicate formLabels = StringPredicate.TRUE;
        private StringPredicate types = StringPredicate.TRUE;
        private StatsPredicate baseStats = StatsPredicate.TRUE;
        private Boolean hasEvolution = null;
        private Boolean hasPreEvolution = null;

        public Builder species (StringPredicate species) {
            this.speciesId = species;
            return this;
        }

        public Builder species (StringPredicate.Builder builder) {
            this.speciesId = builder.build();
            return this;
        }

        public Builder species (String showdownId) {
            this.speciesId = StringPredicate.builder().whitelist(showdownId).build();
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

        public Builder form (String showdownId) {
            this.formId = StringPredicate.builder().whitelist(showdownId).build();
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

        public Builder speciesForm (String showdownId) {
            this.speciesFormId = StringPredicate.builder().whitelist(showdownId).build();
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

        public Builder speciesLabels (String label) {
            this.speciesLabels = StringPredicate.builder().whitelist(label).build();
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

        public Builder formLabels (String label) {
            this.formLabels = StringPredicate.builder().whitelist(label).build();
            return this;
        }

        public Builder type (StringPredicate type) {
            this.types = type;
            return this;
        }

        public Builder type (StringPredicate.Builder builder) {
            this.types = builder.build();
            return this;
        }

        public Builder type (String showdownId) {
            this.types = StringPredicate.builder().whitelist(showdownId).build();
            return this;
        }

        public Builder type (ElementalType type) {
            this.types = StringPredicate.builder().whitelist(type.showdownId()).build();
            return this;
        }

        public Builder baseStats (StatsPredicate stats) {
            this.baseStats = stats;
            return this;
        }

        public Builder baseStats (StatsPredicate.Builder builder) {
            this.baseStats = builder.build();
            return this;
        }

        public Builder hasEvolution (boolean hasEvolution) {
            this.hasEvolution = hasEvolution;
            return this;
        }

        public Builder hasPreEvolution (boolean hasPreEvolution) {
            this.hasPreEvolution = hasPreEvolution;
            return this;
        }

        public PokemonSpeciesPredicate build () {
            return this.get();
        }

        public PokemonSpeciesPredicate get () {
            return new PokemonSpeciesPredicate(
                Objects.requireNonNull(this.speciesId),
                Objects.requireNonNull(this.formId),
                Objects.requireNonNull(this.speciesFormId),
                Objects.requireNonNull(this.speciesLabels),
                Objects.requireNonNull(this.formLabels),
                Objects.requireNonNull(this.types),
                Objects.requireNonNull(this.baseStats),
                Optional.ofNullable(this.hasEvolution),
                Optional.ofNullable(this.hasPreEvolution)
            );
        }
    }
}
