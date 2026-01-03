package com.provismet.cobblemon.lilycobble.pokemon.matcher;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public record HeldItemPredicate (List<String> whitelist, List<String> blacklist) implements Predicate<ItemStack> {
    public static final Codec<HeldItemPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.listOf().optionalFieldOf("whitelist", List.of()).forGetter(HeldItemPredicate::whitelist),
        Codec.STRING.listOf().optionalFieldOf("blacklist", List.of()).forGetter(HeldItemPredicate::blacklist)
    ).apply(instance, HeldItemPredicate::new));

    public static final HeldItemPredicate TRUE = new HeldItemPredicate(List.of(), List.of());
    public static final HeldItemPredicate FALSE = new HeldItemPredicate(List.of("dummy"), List.of("dummy"));

    public static Builder builder () {
        return new Builder();
    }

    @Override
    public boolean test (@Nullable ItemStack stack) {
        String showdownId = stack == null ? null : CobblemonHeldItemManager.INSTANCE.showdownId(stack);
        if (!this.whitelist.isEmpty() && (showdownId == null || !this.whitelist.contains(showdownId))) return false;
        return showdownId == null || !this.blacklist.contains(showdownId);
    }

    public static class Builder {
        private final List<String> required = new ArrayList<>();
        private final List<String> blacklist = new ArrayList<>();

        public Builder require (String showdownId) {
            this.required.add(showdownId);
            return this;
        }

        public Builder require (ItemStack stack) {
            String showdownId = CobblemonHeldItemManager.INSTANCE.showdownId(stack);
            if (showdownId != null) this.required.add(showdownId);
            return this;
        }

        public Builder blacklist (String showdownId) {
            this.blacklist.add(showdownId);
            return this;
        }

        public Builder blacklist (ItemStack stack) {
            String showdownId = CobblemonHeldItemManager.INSTANCE.showdownId(stack);
            if (showdownId != null) this.blacklist.add(showdownId);
            return this;
        }

        public HeldItemPredicate build () {
            return new HeldItemPredicate(
                this.required,
                this.blacklist
            );
        }
    }
}
