package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item.TooltipContext;

/*
 * Lightweight wrapper for an item stack as a hashable object suitable for use as keys in maps.<br>
 * Uses item, item damage, and nbt-tag for hash-code.<br>
 * Ignores quantity.<br>
 * Immutable.
 *
 * @author Shadowmage
 */
public final class ItemHashEntry {
	private final CompoundTag itemTag;
	private ItemStack cacheStack = ItemStack.EMPTY;
	private String cachedNameAndTooltip = "";

	private final HolderLookup.Provider provider;

	/*
	 * @param item MUST NOT BE NULL
	 */
	public ItemHashEntry(ItemStack item) {
		this.provider = BuiltInRegistries.ITEM.asLookup();
		ItemStack copy = item.copy();
		copy.setCount(1);
		itemTag = (CompoundTag) copy.save(provider);
	}

	public ItemHashEntry(ItemStack item, HolderLookup.Provider provider) {
		this.provider = provider;
		ItemStack copy = item.copy();
		copy.setCount(1);
		itemTag = (CompoundTag) copy.save(provider);
	}

	@Override
	public int hashCode() {
		return itemTag.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		//noinspection SimplifiableIfStatement
		if (!(obj instanceof ItemHashEntry)) {
			return false;
		}
		return itemTag.equals(((ItemHashEntry) obj).itemTag);
	}

	public ItemStack getItemStack() {
		if (cacheStack.isEmpty()) {
			cacheStack = ItemStack.parse(provider, itemTag).orElse(ItemStack.EMPTY);
		}
		return cacheStack;
	}

	@OnlyIn(Dist.CLIENT)
	public String getNameAndTooltip() {
		if (cachedNameAndTooltip.isEmpty()) {
			String stackText = getItemStack().getHoverName().getString().toLowerCase() + " ";
			stackText += String.join(" ", getItemStack().getTooltipLines(Item.TooltipContext.of(Minecraft.getInstance().level), Minecraft.getInstance().player, TooltipFlag.Default.NORMAL).stream().map(c -> c.getString()).toList()).toLowerCase();

			cachedNameAndTooltip = stackText;
		}

		return cachedNameAndTooltip;
	}

	public CompoundTag save() {
		return itemTag.copy();
	}

	public static ItemHashEntry readFromNBT(CompoundTag tag) {
		return new ItemHashEntry(ItemStack.parse(BuiltInRegistries.ITEM.asLookup(), tag).orElse(ItemStack.EMPTY), BuiltInRegistries.ITEM.asLookup());
	}
}
