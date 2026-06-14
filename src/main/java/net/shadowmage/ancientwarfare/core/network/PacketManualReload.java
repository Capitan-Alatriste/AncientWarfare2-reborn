package net.shadowmage.ancientwarfare.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

import net.shadowmage.ancientwarfare.core.manual.ManualContentRegistry;
import net.shadowmage.ancientwarfare.core.registry.RegistryLoader;

public record PacketManualReload() implements PacketBase {

    public static final CustomPacketPayload.Type<PacketManualReload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ancientwarfare", "manual_reload"));

    public static final StreamCodec<FriendlyByteBuf, PacketManualReload> CODEC = StreamCodec.unit(new PacketManualReload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void execute(Player player) {
        ManualContentRegistry.clearContents();
        RegistryLoader.reload("manual_content");

        // Ensure we are safely sending the message (if on logical client)
        if (player.level().isClientSide) {
            player.sendSystemMessage(Component.literal("Manual content reloaded"));
        }
    }
}
