package com.provismet.cobblemon.lilycobble.imixin;

import java.util.Set;

public interface IMixinPokemonBattle {
    void lilycobble$addTag(String tag);
    void lilycobble$removeTag(String tag);
    Set<String> lilycobble$getTags();
}
