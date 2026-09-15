package com.psychananaz.psymod.item;

import com.psychananaz.psymod.PsyModUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public final class ModItemIds {
	private ModItemIds() {
	}

	public static ResourceKey<Item> create(String name) {
		return ResourceKey.create(Registries.ITEM, PsyModUtils.getId(name));
	}
}
