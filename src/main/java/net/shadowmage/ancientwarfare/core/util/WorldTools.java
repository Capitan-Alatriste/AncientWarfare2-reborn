package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.Optional;

public class WorldTools {
	private WorldTools() {
	}

	public static <T extends BlockEntity> Optional<T> getTile(Level world, BlockPos pos, Class<T> clazz) {
		if (world.isLoaded(pos)) {
			BlockEntity tile = world.getBlockEntity(pos);
			if (clazz.isInstance(tile)) {
				return Optional.of(clazz.cast(tile));
			}
		}
		return Optional.empty();
	}

	public static Optional<IItemHandler> getInventory(Level world, BlockPos pos, Direction side) {
		return getTile(world, pos, BlockEntity.class).flatMap(t -> InventoryTools.getItemHandlerFrom(t, side));
	}
}
