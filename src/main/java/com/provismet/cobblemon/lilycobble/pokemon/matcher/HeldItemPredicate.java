package com.provismet.cobblemon.lilycobble.pokemon.matcher;

import com.cobblemon.mod.common.pokemon.helditem.CobblemonHeldItemManager;
import com.mojang.serialization.Codec;
import com.provismet.cobblemon.lilycobble.LilyCobbleMain;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A StringPredicate wrapper that tests the ShowdownId of an item.
 */
@SuppressWarnings("unused")
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

    @Override
    public boolean equals (Object object) {
        return object instanceof HeldItemPredicate other && Objects.equals(this.stringPredicate, other.stringPredicate);
    }

    @Override
    public int hashCode () {
        return Objects.hash(this.stringPredicate);
    }

    public static class Builder implements Supplier<HeldItemPredicate> {
        private final StringPredicate.Builder underlying = StringPredicate.builder();

        public Builder whitelist (String showdownId) {
            this.underlying.whitelist(showdownId);
            return this;
        }

        public Builder whitelist (ItemStack stack) {
            String showdownId = CobblemonHeldItemManager.INSTANCE.showdownId(stack);
            if (showdownId != null) this.underlying.whitelist(showdownId);
            else if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                LilyCobbleMain.LOGGER.warn(
                    "HeldItemPredicate builder attempted to whitelist the Showdown ID for {} but found null. If this occurred during datagen, then CobblemonHeldItemManager probably hasn't initialised. Use string literals instead.",
                    stack.getName().getString()
                );
            }
            return this;
        }

        public Builder blacklist (String showdownId) {
            this.underlying.blacklist(showdownId);
            return this;
        }

        public Builder blacklist (ItemStack stack) {
            String showdownId = CobblemonHeldItemManager.INSTANCE.showdownId(stack);
            if (showdownId != null) this.underlying.blacklist(showdownId);
            else if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
                LilyCobbleMain.LOGGER.warn(
                    "HeldItemPredicate builder attempted to blacklist the Showdown ID for {} but found null. If this occurred during datagen, then CobblemonHeldItemManager probably hasn't initialised. Use string literals instead.",
                    stack.getName().getString()
                );
            }
            return this;
        }

        public HeldItemPredicate build () {
            return new HeldItemPredicate(this.underlying.build());
        }

        @Override
        public HeldItemPredicate get() {
            return this.build();
        }
    }
}
