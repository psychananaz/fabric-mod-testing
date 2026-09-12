package com.psychananaz.psymod;

import com.psychananaz.psymod.feature.autotool.EfficientToolSwitcher;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class PsyModClient implements ClientModInitializer {
	public static void showNotification(Component message) {
		Minecraft client = Minecraft.getInstance();
		if (GlobalConfig.INSTANCE.showNotifications && client.player != null) {
			client.gui.hud.setOverlayMessage(message, false);
		}
	}

	@Override
	public void onInitializeClient() {
		ModConfig.INSTANCE.load();
		EfficientToolSwitcher.initialize();
	}
}
