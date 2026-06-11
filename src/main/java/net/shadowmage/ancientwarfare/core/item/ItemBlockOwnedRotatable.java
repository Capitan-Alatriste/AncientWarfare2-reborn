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
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.util.WorldTools;

// TODO Phase 5: import static net.shadowmage.ancientwarfare.core.render.property.CoreProperties.FACING;

public class ItemBlockOwnedRotatable extends ItemBlockBase {
	private IRotatableBlock rotatable;

	public <T extends Block & IRotatableBlock> ItemBlockOwnedRotatable(T block, Item.Properties properties) {
		super(block, properties);
		rotatable = block;
	}

    public <T extends Block & IRotatableBlock> ItemBlockOwnedRotatable(T block) {
        super(block, new Item.Properties());
        rotatable = block;
    }

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        // TODO Phase 5: Direction facing = BlockRotationHandler.getFaceForPlacement(context.getPlayer(), rotatable, context.getClickedFace());
        // TODO Phase 5: boolean val = super.placeBlock(context, state.setValue(FACING, facing));
		boolean val = super.placeBlock(context, state);

		if (val && context.getPlayer() != null) {
			WorldTools.getTile(context.getLevel(), context.getClickedPos(), IOwnable.class).ifPresent(t -> t.setOwner(context.getPlayer()));
		}
		return val;
	}
}
