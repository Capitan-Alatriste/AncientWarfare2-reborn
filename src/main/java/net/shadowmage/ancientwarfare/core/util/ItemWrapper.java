package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ItemWrapper {
	public final Item item;
	public final int damage;

	public ItemWrapper(Item item, int damage) {
		this.item = item;
		this.damage = damage;
	}

	public static ArrayList<ItemWrapper> buildList(String listName, String[] input) {
		ArrayList<ItemWrapper> outputList = new ArrayList<>();

		System.out.println("Building " + listName + "...");

		for (String itemName : input) {
			itemName = itemName.trim();
			if (!itemName.equals("")) {
				String[] itemId = itemName.split(":");
				if (Array.getLength(itemId) != 2 && Array.getLength(itemId) != 3) {
					System.out.println(" - Invalid item (bad length of " + Array.getLength(itemId) + "): " + itemName);
					continue;
				}
				if (itemId[0] == null || itemId[1] == null) {
					System.out.println(" - Invalid block (parse/format error): " + itemName);
					continue;
				}

				Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId[0] + ":" + itemId[1]));

				if (item == null) {
					System.out.println(" - Skipping missing item: " + itemName);
					continue;
				}
				short damage = -1;
				if (Array.getLength(itemId) == 3) {
					try {
						damage = Short.parseShort(itemId[2]);
					}
					catch (NumberFormatException e) {
						System.out.println(" - Damage value invalid : '" + itemId[2] + "', must be a number between 0 and " + Short.MAX_VALUE);
						continue;
					}
				}
				outputList.add(new ItemWrapper(item, damage));
			}
		}

		System.out.println("...added " + outputList.size() + " items to " + listName);

		return outputList;
	}
}
