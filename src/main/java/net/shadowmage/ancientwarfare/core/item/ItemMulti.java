package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
// TODO Phase 9: import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
// TODO Phase 9: import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
// TODO Phase 8: import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

import java.util.HashMap;
import java.util.Map;

/*
 * Handle subtypes through ItemStack damage values in 1.12.
 * In 1.21.1, this is usually achieved by creating separate Items instead of using metadata.
 * For migration sake, we will leave the class and adjust standard methods.
 * Heavy refactoring is required later if items are meant to be separated.
 */
public class ItemMulti extends ItemBase /* implements IClientRegister */ {

	private final HashMap<Integer, String> subItems = new HashMap<>();

	public ItemMulti(Item.Properties properties) {
		super(properties);
	}

    // Kept for legacy compatibility during migration
    public ItemMulti(String modID, String regName) {
        super(new Item.Properties());
    }

	public void addSubItem(int num, String modelName) {
		if (!subItems.containsKey(num))
			subItems.put(num, modelName);
	}

	public void addSubItem(int num, String modelName, String ore) {
		addSubItem(num, modelName);
		// TODO Tags: Add to ItemTags instead of OreDictionary
	}

	public ItemStack getSubItem(int num) {
        // Warning: Damage no longer defines sub-types in 1.21.1
		return new ItemStack(this, 1);
	}

	// TODO Phase 9:
	// public ItemMulti listenToProxy(CommonProxyBase proxy) {
	// 	proxy.addClientRegister(this);
	// 	return this;
	// }

	// @Override
	// public void registerClient() {
	// 	for (Map.Entry<Integer, String> entry : subItems.entrySet()) {
	// 		ModelLoaderHelper.registerItem(this, entry.getKey(), entry.getValue());
	// 	}
	// }
}
