package com.provismet.cobblemon.lilycobble.mixin;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.provismet.cobblemon.lilycobble.imixin.IMixinPokemonBattle;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashSet;
import java.util.Set;

@Mixin(value = PokemonBattle.class, remap = false)
public class PokemonBattleMixin implements IMixinPokemonBattle {
    @Unique private final Set<String> tags = new HashSet<>();

    @Override
    public void lilycobble$addTag (String tag) {
        this.tags.add(tag);
    }

    @Override
    public void lilycobble$removeTag (String tag) {
        this.tags.remove(tag);
    }

    @Override @NotNull
    public Set<String> lilycobble$getTags () {
        return this.tags;
    }
}
