package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.api.moves.MoveTemplate;
import com.cobblemon.mod.common.api.moves.Moves;
import com.cobblemon.mod.common.api.pokemon.Natures;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Gender;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.dynamic.Codecs;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

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
    Optional<Boolean> shiny
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
        Codec.BOOL.optionalFieldOf("shiny").forGetter(PokemonSupplier::shiny)
    ).apply(instance, PokemonSupplier::new));

    /**
     * Constructs a new pokemon properties object using the data from this supplier. Does not include held items.
     *
     * @return A new PokemonProperties object.
     */
    public PokemonProperties toProperties () {
        PokemonProperties properties = new PokemonProperties();
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
        Pokemon pokemon = PokemonProperties.Companion.parse(this.species).create();
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
}
