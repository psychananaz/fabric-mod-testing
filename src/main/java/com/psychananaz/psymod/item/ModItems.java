package com.psychananaz.psymod.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public final class ModItems {
	private ModItems() {
	}

	public static Item register(
			ResourceKey<Item> itemKey,
			Function<Item.Properties, Item> itemFactory,
			Item.Properties settings) {
		Item item = itemFactory.apply(settings.setId(itemKey));
		Registry.register(BuiltInRegistries.ITEM, itemKey, item);
		return item;
	}

	public static void initialize() {
	}
}
