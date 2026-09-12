package com.psychananaz.psymod;

import com.psychananaz.psymod.feature.autotool.EfficientToolSwitcher;
import net.fabricmc.api.ClientModInitializer;

public final class PsyModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		EfficientToolSwitcher.initialize();
	}
}
