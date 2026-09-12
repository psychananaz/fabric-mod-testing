package com.psychananaz.psymod;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

import java.util.Objects;
import java.util.function.UnaryOperator;

import static net.minecraft.network.chat.Component.literal;

public final class ModHelper {
	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(PsyMod.MOD_ID, path);
	}


	public static Component simpleText(String text, TextColor textColor) {
		return styledText(text, style -> style.withColor(
				Objects.requireNonNull(textColor, "textColor")));
	}

	public static Component simpleText(String text) {
		return literal(Objects.requireNonNull(text, "text"));
	}

	/** Creates text with a reusable style transformation. */
	public static Component styledText(String text, UnaryOperator<Style> styleModifier) {
		return literal(Objects.requireNonNull(text, "text"))
				.withStyle(Objects.requireNonNull(styleModifier, "styleModifier"));
	}
}
