package com.provismet.cobblemon.lilycobble.pokemon.matcher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public record StringPredicate (List<String> whitelist, List<String> blacklist) implements Predicate<String> {
        public static final Codec<StringPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().optionalFieldOf("whitelist", List.of()).forGetter(StringPredicate::whitelist),
            Codec.STRING.listOf().optionalFieldOf("blacklist", List.of()).forGetter(StringPredicate::blacklist)
        ).apply(instance, StringPredicate::new));

        public static final StringPredicate TRUE = new StringPredicate(List.of(), List.of());
        public static final StringPredicate FALSE = new StringPredicate(List.of("dummy"), List.of("dummy"));

        public static Builder builder () {
            return new Builder();
        }

        public boolean test (String value) {
            if (!this.whitelist.isEmpty() && !this.whitelist.contains(value)) return false;
            return this.blacklist.isEmpty() || !this.blacklist.contains(value);
        }

        public boolean test (Collection<String> values) {
            if (!this.whitelist.isEmpty() && !values.containsAll(this.whitelist)) return false;
            return this.blacklist.isEmpty() || values.stream().noneMatch(this.blacklist::contains);
        }

        public static class Builder {
            private final Set<String> required = new HashSet<>();
            private final Set<String> blacklisted = new HashSet<>();

            public Builder require (String value) {
                this.required.add(value);
                return this;
            }

            public Builder blacklist (String value) {
                this.blacklisted.add(value);
                return this;
            }

            public StringPredicate build () {
                return new StringPredicate(this.required.stream().toList(), this.blacklisted.stream().toList());
            }
        }
    }