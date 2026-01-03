package com.provismet.cobblemon.lilycobble;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LilyCobbleMain {
	public static final String MODID = "lilycobble";
	public static final Logger LOGGER = LoggerFactory.getLogger("LilyCobble");

    public static Identifier identifier (String path) {
        return Identifier.of(MODID, path);
    }
}