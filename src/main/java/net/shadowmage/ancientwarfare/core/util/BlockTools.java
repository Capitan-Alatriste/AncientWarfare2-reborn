package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

public class BlockTools {
	private BlockTools() {
	}

	public static boolean canPlayerHarvest(Player player, Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.AIR) {
			return false;
		}
		if (player.isCreative()) {
			return true;
		}
		if (world instanceof ServerLevel serverLevel) {
            BlockEvent.BreakEvent event = new BlockEvent.BreakEvent(serverLevel, pos, state, player);
            NeoForge.EVENT_BUS.post(event);
            if (event.isCanceled()) return false;
        }
        return state.canHarvestBlock(world, pos, player);
	}

	public static void dropItems(Level world, BlockPos pos, List<ItemStack> items) {
		if (items == null) {
			return;
		}
		for (ItemStack stack : items) {
			if (stack != null && !stack.isEmpty()) {
				ItemTools.dropItemInWorld(world, stack, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d);
			}
		}
	}

    public static Direction getOpposite(Direction dir) {
        return dir.getOpposite();
    }

    // TODO Phase X: other complex block rotation mapping and property finding methods.
}
