package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

/**
 * Interface that all core ancient warfare packets should implement.
 * This simplifies migration from the old 1.12.2 PacketBase class.
 */
public interface PacketBase extends CustomPacketPayload {
    /**
     * Executes the packet logic.
     * @param player The player context on which this packet is executed.
     */
    void execute(Player player);
}
