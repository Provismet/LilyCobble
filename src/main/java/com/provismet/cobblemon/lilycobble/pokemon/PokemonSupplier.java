package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A buildable supplier for creating Pokemon and representing them in json without relying on PokemonProperties jank.
 */
public record PokemonSupplier (
    String species,
    FeatureApplicator features,
    int level,
    Optional<List<String>> moves,
    Optional<Gender> gender,
    Optional<Identifier> nature,
    Optional<PokemonStats> ivs,
    Optional<PokemonStats> evs,
    Optional<ItemStack> heldItem,
    float scale,
    Optional<Boolean> shiny,
    Optional<String> propertiesBase
) implements Supplier<Pokemon> {

    public static final Codec<PokemonSupplier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("species").forGetter(PokemonSupplier::species),
        FeatureApplicator.CODEC.optionalFieldOf("features", FeatureApplicator.DEFAULT).forGetter(PokemonSupplier::features),
        Codecs.POSITIVE_INT.optionalFieldOf("level", 1).forGetter(PokemonSupplier::level),
        Codecs.NON_EMPTY_STRING.listOf(1, 4).optionalFieldOf("moves").forGetter(PokemonSupplier::moves),
        Gender.getCODEC().optionalFieldOf("gender").forGetter(PokemonSupplier::gender),
        Identifier.CODEC.optionalFieldOf("nature").forGetter(PokemonSupplier::nature),
        PokemonStats.CODEC.optionalFieldOf("iv").forGetter(PokemonSupplier::ivs),
        PokemonStats.CODEC.optionalFieldOf("ev").forGetter(PokemonSupplier::evs),
        ItemStack.CODEC.optionalFieldOf("held_item").forGetter(PokemonSupplier::heldItem),
        Codecs.POSITIVE_FLOAT.optionalFieldOf("scale", 1f).forGetter(PokemonSupplier::scale),
        Codec.BOOL.optionalFieldOf("shiny").forGetter(PokemonSupplier::shiny),
        Codec.STRING.optionalFieldOf("properties_base").forGetter(PokemonSupplier::propertiesBase)
    ).apply(instance, PokemonSupplier::new));

    /**
     * Constructs a new pokemon properties object using the data from this supplier. Does not include held items.
     *
     * @return A new PokemonProperties object.
     */
    public PokemonProperties toProperties () {
        PokemonProperties properties = this.propertiesBase.map(PokemonProperties.Companion::parse).orElseGet(PokemonProperties::new);
        properties.setSpecies(this.species);
        this.features.apply(properties);
        properties.setLevel(this.level);
        this.moves.ifPresent(properties::setMoves);
        this.gender.ifPresent(properties::setGender);
        this.nature.ifPresent(nature -> properties.setNature(nature.getPath()));
        this.ivs.ifPresent(iv -> properties.setIvs(iv.toIVs()));
        this.evs.ifPresent(ev -> properties.setEvs(ev.toEVs()));
        properties.setScaleModifier(this.scale);
        this.shiny.ifPresent(properties::setShiny);

        return new PokemonProperties();
    }

    @Override
    public Pokemon get () {
        Pokemon pokemon = this.propertiesBase
            .map(properties -> PokemonProperties.Companion.parse(this.species + " " + properties).create())
            .orElseGet(() -> PokemonProperties.Companion.parse(this.species).create());

        this.features.apply(pokemon);
        pokemon.setLevel(this.level);

        if (this.moves.isPresent()) {
            List<MoveTemplate> moves = this.moves.get()
                .stream()
                .limit(4)
                .map(Moves::getByName)
                .filter(Objects::nonNull)
                .toList();

            pokemon.getMoveSet().clear();
            for (int i = 0; i < moves.size(); ++i) {
                pokemon.getMoveSet().setMove(i, moves.get(i).create());
            }
        }

        this.gender.ifPresent(pokemon::setGender);
        this.nature.flatMap(nature -> Optional.ofNullable(Natures.getNature(nature))).ifPresent(pokemon::setNature);
        this.ivs.ifPresent(iv -> iv.applyIVs(pokemon));
        this.evs.ifPresent(ev -> ev.applyEVs(pokemon));
        this.heldItem.ifPresent(pokemon::setHeldItem$common);
        pokemon.setScaleModifier(this.scale);
        this.shiny.ifPresent(pokemon::setShiny);

        pokemon.onChange(null);
        return pokemon;
    }

    public static class Builder implements Supplier<PokemonSupplier> {
        private String species;
        private FeatureApplicator features = FeatureApplicator.DEFAULT;
        private int level = 1;
        List<String> moves;
        Gender gender;
        Identifier nature;
        PokemonStats ivs;
        PokemonStats evs;
        ItemStack heldItem;
        float scale = 1f;
        Boolean shiny;
        String propertiesBase;

        public Builder species (String species) {
            this.species = species;
            return this;
        }

        public Builder features (FeatureApplicator features) {
            this.features = features;
            return this;
        }

        public Builder withFeature (String key, FeatureApplicator.FeatureValue value) {
            this.features = this.features.with(key, value);
            return this;
        }

        public Builder withFeature (String key, String value) {
            this.features = this.features.with(key, value);
            return this;
        }

        public Builder withFeature (String key, boolean value) {
            this.features = this.features.with(key, value);
            return this;
        }

        public Builder withFeature (String key, int value) {
            this.features = this.features.with(key, value);
            return this;
        }

        public Builder level (int level) {
            this.level = level;
            return this;
        }

        public Builder moves (@Nullable List<String> moves) {
            this.moves = moves;
            return this;
        }

        public Builder addMove (String move) {
            if (this.moves == null) this.moves = new ArrayList<>();
            this.moves.add(move);
            return this;
        }

        public Builder gender (@Nullable Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder nature (@Nullable Identifier nature) {
            this.nature = nature;
            return this;
        }

        public Builder nature (Nature nature) {
            this.nature = nature.getName();
            return this;
        }

        public Builder ivs (@Nullable PokemonStats ivs) {
            this.ivs = ivs;
            return this;
        }

        public Builder evs (@Nullable PokemonStats evs) {
            this.evs = evs;
            return this;
        }

        public Builder heldItem (@Nullable ItemStack heldItem) {
            this.heldItem = heldItem;
            return this;
        }

        public Builder scale (float scale) {
            this.scale = scale;
            return this;
        }

        public Builder shiny (@Nullable Boolean shiny) {
            this.shiny = shiny;
            return this;
        }

        public Builder baseProperties (@Nullable String properties) {
            this.propertiesBase = properties;
            return this;
        }

        @Override
        public PokemonSupplier get () {
            return new PokemonSupplier(
                Objects.requireNonNull(this.species),
                Objects.requireNonNull(this.features),
                this.level,
                Optional.ofNullable(this.moves),
                Optional.ofNullable(this.gender),
                Optional.ofNullable(this.nature),
                Optional.ofNullable(this.ivs),
                Optional.ofNullable(this.evs),
                Optional.ofNullable(this.heldItem),
                this.scale,
                Optional.ofNullable(this.shiny),
                Optional.ofNullable(this.propertiesBase)
            );
        }
    }
}
