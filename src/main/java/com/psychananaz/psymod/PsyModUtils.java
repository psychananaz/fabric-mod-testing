package com.psychananaz.psymod;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

public final class PsyModUtils {
	public static Identifier getId(String path) {
		return Identifier.fromNamespaceAndPath(PsyMod.MOD_ID, path);
	}

	public class Txt {
		public static MutableComponent lit(String text) {
			return Component.literal(text);
		}
		public static MutableComponent trans(String key, Object... args) {
			return Component.translatable(key, args);
		}
	}
}
