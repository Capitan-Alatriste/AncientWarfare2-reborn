package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ItemTools {
	private ItemTools() {
	}

	public static void dropItemInWorld(Level world, ItemStack stack, double x, double y, double z) {
		if (stack != null && !stack.isEmpty()) {
			ItemEntity item = new ItemEntity(world, x, y, z, stack);
			item.setDeltaMovement(0, 0, 0);
			world.addFreshEntity(item);
		}
	}

	public static void dropItemInWorld(Level world, ItemStack stack, BlockPos pos) {
		if (stack != null && !stack.isEmpty()) {
			ItemEntity item = new ItemEntity(world, pos.getX() + 0.5d, pos.getY() + 0.5d, pos.getZ() + 0.5d, stack);
			item.setDeltaMovement(0, 0, 0);
			world.addFreshEntity(item);
		}
	}
}
