package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public abstract class ItemBase extends Item {
	// In 1.21.1 RegistryName is no longer set inside the Item constructor
    // UnlocalizedName is derived automatically from the registry ID
	public ItemBase(Item.Properties properties) {
		super(properties);
	}
}
