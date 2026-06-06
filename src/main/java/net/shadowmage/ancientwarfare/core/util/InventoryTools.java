package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import java.util.Optional;

public class InventoryTools {

    // TODO Phase X InventoryTools has dependencies on ItemQuantityMap from core.inventory
    // and complex ore dictionary mapping which will be addressed in the inventory phase.

    public static Optional<IItemHandler> getItemHandlerFrom(BlockEntity tile, Direction side) {
        if (tile != null && tile.getLevel() != null) {
            return Optional.ofNullable(tile.getLevel().getCapability(Capabilities.ItemHandler.BLOCK, tile.getBlockPos(), side));
        }
        return Optional.empty();
    }
}
