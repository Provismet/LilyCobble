package com.provismet.cobblemon.lilycobble.pokemon.matcher;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A predicate to run on a string or collection of strings, checking if they belong to a whitelist or blacklist.
 *
 * @param whitelist The list of allowed strings.
 * @param blacklist The list of banned strings.
 * @param whitelistIsSubset Used only for testing collections, if true the whitelist is treated as a list of "required" strings instead and the collection will be allowed to have strings outside the whitelist.
 */
public record StringPredicate (List<String> whitelist, List<String> blacklist, boolean whitelistIsSubset) implements Predicate<String> {
    public static final Codec<StringPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().optionalFieldOf("whitelist", List.of()).forGetter(StringPredicate::whitelist),
        Codec.STRING.listOf().optionalFieldOf("blacklist", List.of()).forGetter(StringPredicate::blacklist),
        Codec.BOOL.optionalFieldOf("whitelist_is_subset", false).forGetter(StringPredicate::whitelistIsSubset)
    ).apply(instance, StringPredicate::new));

    public static final StringPredicate TRUE = new StringPredicate(List.of(), List.of(), false);
    public static final StringPredicate FALSE = new StringPredicate(List.of("dummy"), List.of("dummy"), false);

    public static Builder builder () {
        return new Builder();
    }

    public boolean test (@Nullable String value) {
        if (!this.whitelist.isEmpty() && (value == null || !this.whitelist.contains(value))) return false;
        return value == null || this.blacklist.isEmpty() || !this.blacklist.contains(value);
    }

    public boolean test (Collection<String> values) {
        if (this.whitelistIsSubset) return this.testRequired(values);
        return this.testAll(values);
    }

    private boolean testAll (Collection<String> values) {
        for (String string : values) {
            if (!this.test(string)) return false;
        }
        return true;
    }

    private boolean testRequired (Collection<String> values) {
        if (!this.whitelist.isEmpty() && !values.containsAll(this.whitelist)) return false;
        return this.blacklist.isEmpty() || values.stream().noneMatch(this.blacklist::contains);
    }

    public static class Builder {
        private final Set<String> whitelisted = new HashSet<>();
        private final Set<String> blacklisted = new HashSet<>();
        private boolean whitelistIsSubset = false;

        public Builder whitelist(String value) {
            this.whitelisted.add(value);
            return this;
        }

        public Builder blacklist (String value) {
            this.blacklisted.add(value);
            return this;
        }

        public Builder whitelistAsSubset (boolean value) {
            this.whitelistIsSubset = value;
            return this;
        }

        public StringPredicate build () {
            return new StringPredicate(this.whitelisted.stream().toList(), this.blacklisted.stream().toList(), whitelistIsSubset);
        }
    }
}