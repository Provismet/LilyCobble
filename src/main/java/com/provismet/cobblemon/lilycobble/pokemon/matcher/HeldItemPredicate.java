package com.provismet.cobblemon.lilycobble.pokemon.matcher;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * A StringPredicate wrapper that tests the ShowdownId of an item.
 */
public record HeldItemPredicate (StringPredicate stringPredicate) implements Predicate<ItemStack> {
    public static final Codec<HeldItemPredicate> CODEC = StringPredicate.CODEC.xmap(HeldItemPredicate::new, HeldItemPredicate::stringPredicate);
    public static final HeldItemPredicate TRUE = new HeldItemPredicate(StringPredicate.TRUE);
    public static final HeldItemPredicate FALSE = new HeldItemPredicate(StringPredicate.FALSE);

    public static Builder builder () {
        return new Builder();
    }

    @Override
    public boolean test (@Nullable ItemStack stack) {
        String showdownId = stack == null ? null : CobblemonHeldItemManager.INSTANCE.showdownId(stack);
        return this.stringPredicate.test(showdownId);
    }

    public static class Builder {
        private final StringPredicate.Builder underlying = StringPredicate.builder();

        public Builder whitelist (String showdownId) {
            this.underlying.whitelist(showdownId);
            return this;
        }

        public Builder whitelist (ItemStack stack) {
            String showdownId = CobblemonHeldItemManager.INSTANCE.showdownId(stack);
            if (showdownId != null) this.underlying.whitelist(showdownId);
            return this;
        }

        public Builder blacklist (String showdownId) {
            this.underlying.blacklist(showdownId);
            return this;
        }

        public Builder blacklist (ItemStack stack) {
            String showdownId = CobblemonHeldItemManager.INSTANCE.showdownId(stack);
            if (showdownId != null) this.underlying.blacklist(showdownId);
            return this;
        }

        public HeldItemPredicate build () {
            return new HeldItemPredicate(this.underlying.build());
        }
    }
}
