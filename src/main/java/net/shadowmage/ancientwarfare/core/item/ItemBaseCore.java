package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.world.item.Item;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
// TODO Phase 9: import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
// TODO Phase 8: import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

public class ItemBaseCore extends ItemBase /* implements IClientRegister */ {
	public ItemBaseCore(Item.Properties properties) {
		super(properties);

        // TODO Phase 9: AncientWarfareCore.proxy.addClientRegister(this);
	}

    // Kept for legacy compatibility if instantiated without properties
    public ItemBaseCore(String regName) {
        super(new Item.Properties());
    }

	// TODO Phase 9:
	// @Override
	// public void registerClient() {
	// 	ModelLoaderHelper.registerItem(this, "core");
	// }
}
