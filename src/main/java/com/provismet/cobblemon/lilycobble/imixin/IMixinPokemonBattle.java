package com.provismet.cobblemon.lilycobble.imixin;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface IMixinPokemonBattle {
    void lilycobble$addTag(String tag);
    void lilycobble$removeTag(String tag);
    @NotNull Set<String> lilycobble$getTags();
}
