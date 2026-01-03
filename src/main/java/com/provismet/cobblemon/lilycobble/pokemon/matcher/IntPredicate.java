package com.provismet.cobblemon.lilycobble.pokemon.matcher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public record IntPredicate (Comparison comparison, int compareTo, Optional<Integer> compareTo2) implements Predicate<Integer> {
    public static final Codec<IntPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Comparison.CODEC.fieldOf("comparison").forGetter(IntPredicate::comparison),
        Codec.INT.fieldOf("compare_to").forGetter(IntPredicate::compareTo),
        Codec.INT.optionalFieldOf("second_compare_to").forGetter(IntPredicate::compareTo2)
    ).apply(instance, IntPredicate::new));

    public static final IntPredicate TRUE = new IntPredicate(Comparison.LESS_THAN_OR_EQUAL_TO, Integer.MAX_VALUE, 0);
    public static final IntPredicate FALSE = new IntPredicate(Comparison.GREATER_THAN, Integer.MAX_VALUE, 0);

    public IntPredicate (Comparison comparison, int compareTo, int compareTo2) {
        this(comparison, compareTo, Optional.of(compareTo2));
    }

    public IntPredicate (Comparison comparison, int compareTo) {
        this(comparison, compareTo, Optional.empty());
    }

    public static IntPredicate equalTo (int value) {
        return new IntPredicate(Comparison.EQUALS, value);
    }

    public static IntPredicate greaterThan (int value) {
        return new IntPredicate(Comparison.GREATER_THAN, value);
    }

    public static IntPredicate greaterThanOrEqualTo (int value) {
        return new IntPredicate(Comparison.GREATER_THAN_OR_EQUAL_TO, value);
    }

    public static IntPredicate lessThan (int value) {
        return new IntPredicate(Comparison.LESS_THAN, value);
    }

    public static IntPredicate lessThanOrEqualTo (int value) {
        return new IntPredicate(Comparison.LESS_THAN_OR_EQUAL_TO, value);
    }

    public static IntPredicate inclusiveRange (int min, int max) {
        return new IntPredicate(Comparison.INCLUSIVE_RANGE, min, max);
    }

    public static IntPredicate exclusiveRange (int min, int max) {
        return new IntPredicate(Comparison.EXCLUSIVE_RANGE, min, max);
    }

    public boolean test (Integer value) {
        if (value == null) return this.equals(TRUE);
        return this.comparison.test(value, this.compareTo, this.compareTo2.orElse(0));
    }

    public enum Comparison {
        EQUALS((num1, num2, num3) -> Objects.equals(num1, num2)),
        GREATER_THAN((num1, num2, num3) -> num1 > num2),
        GREATER_THAN_OR_EQUAL_TO((num1, num2, num3) -> num1 >= num2),
        LESS_THAN((num1, num2, num3) -> num1 < num2),
        LESS_THAN_OR_EQUAL_TO((num1, num2, num3) -> num1 <= num2),
        INCLUSIVE_RANGE((num1, num2, num3) -> num1 >= num2 && num1 <= num3),
        EXCLUSIVE_RANGE((num1, num2, num3) -> num1 > num2 && num1 < num3);

        public static final Codec<Comparison> CODEC = Codec.stringResolver(Enum::name, Comparison::valueOf);

        private final TriIntPredicate predicate;

        Comparison(TriIntPredicate predicate) {
            this.predicate = predicate;
        }

        public boolean test (int num1, int num2, int num3) {
            return this.predicate.test(num1, num2, num3);
        }
    }

    @FunctionalInterface
    public interface TriIntPredicate {
        boolean test (int input, int compareTo, int compareTo2);
    }
}
