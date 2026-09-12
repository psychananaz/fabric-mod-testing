package com.psychananaz.psymod.item;

import com.psychananaz.psymod.ModHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItemIds {
	private ModItemIds() {
	}

	public static ResourceKey<Item> create(String name) {
		return ResourceKey.create(Registries.ITEM, ModHelper.id(name));
	}
}
