package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

public class ItemBlockMeta extends ItemBlockBase {

	public ItemBlockMeta(Block block, Item.Properties properties) {
		super(block, properties);
	}

    // Kept for legacy compatibility during migration
    public ItemBlockMeta(Block block) {
        super(block, new Item.Properties());
    }

	@Override
	public String getDescriptionId(ItemStack stack) {
        // In 1.21.1, metadata doesn't exist on items in the same way, but blocks might use BlockState.
        // Usually, meta item blocks are split into separate items per block in 1.13+.
        // If they are still sharing the same block, we'd need to differentiate by components or just return the base description ID.
		return super.getDescriptionId(stack); // Needs proper blockstate handling if relying on this for translation keys
	}
}
