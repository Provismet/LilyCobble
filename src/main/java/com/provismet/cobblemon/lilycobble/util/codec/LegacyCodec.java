package com.provismet.cobblemon.lilycobble.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import java.util.function.Function;

public record LegacyCodec<T> (Codec<T> standard, Codec<T> legacy, Function<T, Boolean> shouldFallback) implements Codec<T> {
    @Override
    public <U> DataResult<Pair<T, U>> decode (DynamicOps<U> ops, U input) {
        DataResult<Pair<T, U>> standardResult = this.standard.decode(ops, input);
        if (standardResult.isSuccess()) {
            T result = standardResult.result().orElseThrow().getFirst();
            if (!this.shouldFallback.apply(result)) return standardResult;
        }

        DataResult<Pair<T, U>> legacyResult = this.legacy.decode(ops, input);
        if (legacyResult.isSuccess()) return legacyResult;

        if (standardResult.hasResultOrPartial()) return standardResult;
        if (legacyResult.hasResultOrPartial()) return legacyResult;

        return DataResult.error(() -> "Failed to parse either. Standard: " + standardResult.error().orElseThrow().message() + "; Legacy: " + legacyResult.error().orElseThrow().message());
    }

    @Override
    public <U> DataResult<U> encode (T input, DynamicOps<U> ops, U prefix) {
        return this.standard.encode(input, ops, prefix);
    }
}
