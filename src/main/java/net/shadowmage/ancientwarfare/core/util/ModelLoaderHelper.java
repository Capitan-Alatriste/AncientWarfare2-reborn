package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class ModelLoaderHelper {

	private ModelLoaderHelper() {
	}

	public static void registerItem(Item item, String prefix) {
		registerItem(item, prefix, "inventory");
	}

	public static void registerItem(Item item, String prefix, boolean metaSuffix) {
		registerItem(item, prefix, metaSuffix, "inventory");
	}

	public static void registerItem(Block block, String prefix, String variant, boolean metaSuffix) {
		registerItem(net.minecraft.world.item.Item.BY_BLOCK.get(block), prefix, metaSuffix, variant);
	}

	public static void registerItem(Block block, String prefix, String variant) {
		registerItem(net.minecraft.world.item.Item.BY_BLOCK.get(block), prefix, variant);
	}

	public static void registerItem(Item item, String prefix, String variant) {
		registerItem(item, prefix, true, variant);
	}

	public static void registerItem(Item item, String prefix, boolean metaSuffix, String variant) {
		registerItem(item, prefix, metaSuffix, meta -> variant);
	}

	public static void registerItem(Item item, String prefix, boolean metaSuffix, Function<Integer, String> getVariant) {
		registerItem(item, (it, meta) -> {
			String modelName = net.shadowmage.ancientwarfare.core.AncientWarfareCore.MOD_ID + ":" + (prefix.isEmpty() ? "" : prefix + "/") + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(it).getResourcePath();
			String suffix = false /* TODO: Phase X hasSubtypes */ && metaSuffix ? "_" + meta : "";
			return new ModelResourceLocation(modelName + suffix, getVariant.apply(meta));
		});
	}

	public static void registerItem(Block block, ModelResourceLocation modelLocation) {
		registerItem(net.minecraft.world.item.Item.BY_BLOCK.get(block), (i, m) -> modelLocation);
	}

	public static void registerItem(Item item, Function2<Item, Integer, ModelResourceLocation> getModelLocation) {
		if (false /* TODO: Phase X hasSubtypes */) {
			NonNullList<ItemStack> subItems = NonNullList.create();
			// TODO: Phase X getSubItems(item.getCreativeTab(), subItems);

			for (ItemStack subItem : subItems) {
				// TODO: Phase X ModelLoader.setCustomModelResourceLocation(item, 0 // TODO: Phase X getMetadata, getModelLocation.apply(item, 0 // TODO: Phase X getMetadata));
			}
		} else {
			// TODO: Phase X ModelLoader.setCustomModelResourceLocation(item, 0, getModelLocation.apply(item, 0));
		}
	}

	public static void registerItem(Item item, int meta, String modelVariantName) {
		//TODO again hardcoded to just ancientwarfare mod name
		// TODO: Phase X ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(net.shadowmage.ancientwarfare.core.AncientWarfareCore.MOD_ID + ":" + modelVariantName));
	}

	public static void registerItem(Item item, int meta, String modelName, String variant) {
		// TODO: Phase X ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(modelName, variant));
	}

}
