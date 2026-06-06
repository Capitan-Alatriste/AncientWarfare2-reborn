package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

public class RegistryTools {
	public static String getItemName(Item item) {
		if (item == null || item == Items.AIR) {
			return "";
		}
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}

	public static String getBlockName(Block block) {
		if (block == null || block == Blocks.AIR) {
			return "";
		}
		return BuiltInRegistries.BLOCK.getKey(block).toString();
	}

	public static Item getItem(String name) {
		return BuiltInRegistries.ITEM.get(ResourceLocation.parse(name));
	}

	public static Block getBlock(String name) {
		return BuiltInRegistries.BLOCK.get(ResourceLocation.parse(name));
	}
}
