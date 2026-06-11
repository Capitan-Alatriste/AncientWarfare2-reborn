package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

public class ItemBlockBase extends BlockItem {
	public ItemBlockBase(Block block, Item.Properties properties) {
		super(block, properties);
	}

    // Kept for legacy code compatibility during migration
    public ItemBlockBase(Block block) {
        super(block, new Item.Properties());
    }
}
