package net.shadowmage.ancientwarfare.core.network;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.world.entity.player.Player;

public class PacketHandler {

    /**
     * Common handler for all packets that implement PacketBase.
     * This dispatches the execution logic inside the packet on the correct thread.
     */
    public static void handle(PacketBase payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            if (player != null) {
                payload.execute(player);
            }
        });
    }
}
