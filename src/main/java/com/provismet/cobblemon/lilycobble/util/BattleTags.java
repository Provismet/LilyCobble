package com.provismet.cobblemon.lilycobble.util;

import com.cobblemon.mod.common.api.battles.model.PokemonBattle;
import com.provismet.cobblemon.lilycobble.imixin.IMixinPokemonBattle;

/**
 * Utility methods to add, remove, and query string-based tags regarding battles.
 * <p>
 * This can be used to apply metadata to a battle for sidemods.
 */
@SuppressWarnings("unused")
public interface BattleTags {
    static void add (PokemonBattle battle, String tag) {
        if (battle instanceof IMixinPokemonBattle mixinBattle) {
            mixinBattle.lilycobble$addTag(tag);
        }
    }

    static void remove (PokemonBattle battle, String tag) {
        if (battle instanceof IMixinPokemonBattle mixinBattle) {
            mixinBattle.lilycobble$removeTag(tag);
        }
    }

    static boolean has (PokemonBattle battle, String tag) {
        if (battle instanceof IMixinPokemonBattle mixinBattle) {
            return mixinBattle.lilycobble$getTags().contains(tag);
        }
        return false;
    }
}
