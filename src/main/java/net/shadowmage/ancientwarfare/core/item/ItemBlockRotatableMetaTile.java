package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ItemBlockRotatableMetaTile extends ItemBlockBase {

	private IRotatableBlock rotatable;

	public ItemBlockRotatableMetaTile(Block block, Item.Properties properties) {
		super(block, properties);
		if (!(block instanceof IRotatableBlock)) {
			throw new IllegalArgumentException("Must be a rotatable block!!");
		}
		rotatable = (IRotatableBlock) block;
	}

    public ItemBlockRotatableMetaTile(Block block) {
        super(block, new Item.Properties());
		if (!(block instanceof IRotatableBlock)) {
			throw new IllegalArgumentException("Must be a rotatable block!!");
		}
		rotatable = (IRotatableBlock) block;
    }

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		boolean val = super.placeBlock(context, state);
		if (val) {
			BlockEntity te = context.getLevel().getBlockEntity(context.getClickedPos());
			if (te instanceof IOwnable && context.getPlayer() != null) {
				((IOwnable) te).setOwner(context.getPlayer());
			}
			if (te instanceof IRotatableTile && context.getPlayer() != null) {
                // TODO Phase 5: BlockRotationHandler should be checked.
				((IRotatableTile) te).setPrimaryFacing(BlockRotationHandler.getFaceForPlacement(context.getPlayer(), rotatable, context.getClickedFace()));
			}
			BlockTools.notifyBlockUpdate(context.getLevel(), context.getClickedPos());
		}
		return val;
	}

	@Override
	public String getDescriptionId(ItemStack stack) {
		return super.getDescriptionId(stack);
	}

}
