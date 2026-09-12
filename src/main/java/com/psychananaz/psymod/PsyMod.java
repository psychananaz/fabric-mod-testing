package com.psychananaz.psymod;

import com.psychananaz.psymod.feature.ModFeatures;
import com.psychananaz.psymod.item.ModItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PsyMod implements ModInitializer {
	public static final String MOD_ID = "psymod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static void logVerbose(String message, Object... arguments) {
		if (GlobalConfig.INSTANCE.verboseLogging) {
			LOGGER.info(message, arguments);
		}
	}

	@Override
	public void onInitialize() {
		GlobalConfig.INSTANCE.load();
		ModItems.initialize();
		ModFeatures.initialize();
		LOGGER.info("Initialized {}", MOD_ID);
	}
}
