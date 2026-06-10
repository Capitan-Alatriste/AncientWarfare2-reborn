package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

public class ItemBlockOwned extends ItemBlockBase {

	public ItemBlockOwned(Block block, Item.Properties properties) {
		super(block, properties);
	}

    public ItemBlockOwned(Block block) {
        super(block, new Item.Properties());
    }

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		boolean val = super.placeBlock(context, state);
		if (val && context.getPlayer() != null) {
			WorldTools.getTile(context.getLevel(), context.getClickedPos(), IOwnable.class).ifPresent(t -> t.setOwner(context.getPlayer()));
		}
		return val;
	}
}
