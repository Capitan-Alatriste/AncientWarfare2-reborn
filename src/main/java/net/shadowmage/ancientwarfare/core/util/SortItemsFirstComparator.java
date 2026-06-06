package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;

public class SortItemsFirstComparator implements Comparator<ItemStack> {

	private final Item itemFirst;

	public SortItemsFirstComparator(Item item) {
		itemFirst = item;
	}

	@Override
	public int compare(ItemStack o1, ItemStack o2) {
		if (o1.isEmpty() && o2.isEmpty()) {
			return 0;
		}
		if (o1.isEmpty()) {
			return 1;
		}
		if (o2.isEmpty()) {
			return -1;
		}
		if (o1.getItem() == itemFirst) {
			if (o2.getItem() == itemFirst) {
				return 0;
			}
			return -1;
		}
		if (o2.getItem() == itemFirst) {
			return 1;
		}
		return 0;
	}
}
