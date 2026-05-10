package com.provismet.cobblemon.lilycobble.pokemon;

import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.pokemon.feature.FlagSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.IntSpeciesFeature;
import com.cobblemon.mod.common.api.pokemon.feature.StringSpeciesFeature;
import com.cobblemon.mod.common.api.properties.CustomPokemonProperty;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @param featureMap A mapping of feature name to value. All are applied to the Pokémon when triggered.
 */
public record FeatureApplicator (Map<String, FeatureValue> featureMap) {
    public static final Codec<FeatureApplicator> CODEC = Codec.unboundedMap(Codec.STRING, FeatureValue.CODEC).xmap(FeatureApplicator::new, FeatureApplicator::featureMap);

    public static final PacketCodec<RegistryByteBuf, FeatureApplicator> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.map(Object2ObjectOpenHashMap::new, PacketCodecs.STRING, FeatureValue.PACKET_CODEC),
        FeatureApplicator::featureMap,
        FeatureApplicator::new
    );

    public static final FeatureApplicator DEFAULT = new FeatureApplicator(Map.of());

    public static FeatureApplicator single (String name, String value) {
        return new FeatureApplicator(Map.of(name, FeatureValue.of(value)));
    }

    public static FeatureApplicator single (String name, boolean value) {
        return new FeatureApplicator(Map.of(name, FeatureValue.of(value)));
    }

    public static FeatureApplicator single (String name, int value) {
        return new FeatureApplicator(Map.of(name, FeatureValue.of(value)));
    }

    public FeatureApplicator with (String key, FeatureValue value) {
        Map<String, FeatureValue> copy = new HashMap<>(this.featureMap);
        copy.put(Objects.requireNonNull(key), Objects.requireNonNull(value));
        return new FeatureApplicator(copy);
    }

    public FeatureApplicator with (String key, String value) {
        return this.with(key, FeatureValue.of(Objects.requireNonNull(value)));
    }

    public FeatureApplicator with (String key, boolean value) {
        return this.with(key, FeatureValue.of(value));
    }

    public FeatureApplicator with (String key, int value) {
        return this.with(key, FeatureValue.of(value));
    }

    public void apply (Pokemon pokemon) {
        for (Map.Entry<String, FeatureValue> feature : this.featureMap.entrySet()) {
            feature.getValue().getProperty(feature.getKey()).apply(pokemon);
        }
    }

    public void apply (PokemonProperties properties) {
        List<CustomPokemonProperty> toSet = new ArrayList<>();

        for (Map.Entry<String, FeatureValue> feature : this.featureMap.entrySet()) {
            CustomPokemonProperty property = feature.getValue().getProperty(feature.getKey());
            if (property != null) toSet.add(property);
        }

        properties.setCustomProperties(toSet);
    }

    /**
     * A wrapper around strings, booleans, and integers. Allowing any of those primitives to be used in the json.
     * <p>
     * It is recommended to use the static constructors instead of fiddling with the canonical constructor.
     *
     * @param value An Either<> object for the value.
     */
    public record FeatureValue (Either<String, Either<Boolean, Integer>> value) {
        public static final Codec<FeatureValue> CODEC = Codec.either(
            Codecs.NON_EMPTY_STRING,
            Codec.either(Codec.BOOL, Codec.INT)
        ).xmap(FeatureValue::of, FeatureValue::value);

        public static final PacketCodec<RegistryByteBuf, FeatureValue> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.either(PacketCodecs.STRING, PacketCodecs.either(PacketCodecs.BOOL, PacketCodecs.INTEGER)),
            FeatureValue::value,
            FeatureValue::new
        );

        public static FeatureValue of (String value) {
            return new FeatureValue(Either.left(value));
        }

        public static FeatureValue of (boolean value) {
            return new FeatureValue(Either.right(Either.left(value)));
        }

        public static FeatureValue of (int value) {
            return new FeatureValue(Either.right(Either.right(value)));
        }

        public static FeatureValue of (Either<String, Either<Boolean, Integer>> value) {
            return value.map(FeatureValue::of, boolOrInt -> boolOrInt.map(FeatureValue::of, FeatureValue::of));
        }

        public CustomPokemonProperty getProperty (String key) {
            return this.value.map(
                stringVal -> new StringSpeciesFeature(key, stringVal),
                boolOrInt -> boolOrInt.map(
                    boolVal -> new FlagSpeciesFeature(key, boolVal),
                    intVal -> new IntSpeciesFeature(key, intVal)
                )
            );
        }
    }
}
