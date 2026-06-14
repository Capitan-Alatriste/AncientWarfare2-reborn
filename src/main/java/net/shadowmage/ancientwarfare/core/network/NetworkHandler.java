package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {

    public static final NetworkHandler INSTANCE = new NetworkHandler();

    public static final String CHANNELNAME = "ancientwarfare";

    public static final ResourceLocation NETWORK_ID = ResourceLocation.fromNamespaceAndPath("ancientwarfare", "network");

    // TODO Phase 7: GUI/Container definitions moved or refactored?

    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("ancientwarfare");

        registrar.playBidirectional(PacketBlockEvent.TYPE, PacketBlockEvent.CODEC, PacketHandler::handle);
        registrar.playBidirectional(PacketEntity.TYPE, PacketEntity.CODEC, PacketHandler::handle);
        registrar.playBidirectional(PacketGui.TYPE, PacketGui.CODEC, PacketHandler::handle);
        registrar.playBidirectional(PacketItemInteraction.TYPE, PacketItemInteraction.CODEC, PacketHandler::handle);
        registrar.playBidirectional(PacketItemMouseScroll.TYPE, PacketItemMouseScroll.CODEC, PacketHandler::handle);
        registrar.playBidirectional(PacketManualReload.TYPE, PacketManualReload.CODEC, PacketHandler::handle);
        registrar.playBidirectional(PacketResearchInit.TYPE, PacketResearchInit.CODEC, PacketHandler::handle);
        registrar.playBidirectional(PacketResearchStart.TYPE, PacketResearchStart.CODEC, PacketHandler::handle);
        registrar.playBidirectional(PacketResearchUpdate.TYPE, PacketResearchUpdate.CODEC, PacketHandler::handle);
    }

    public static void sendToServer(CustomPacketPayload pkt) {
        PacketDistributor.sendToServer(pkt);
    }

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload pkt) {
        PacketDistributor.sendToPlayer(player, pkt);
    }

    public static void sendToAllPlayers(CustomPacketPayload pkt) {
        PacketDistributor.sendToAllPlayers(pkt);
    }

    public static void sendToAllTracking(Entity e, CustomPacketPayload pkt) {
        PacketDistributor.sendToPlayersTrackingEntity(e, pkt);
    }

    public static void sendToAllTrackingChunk(Level world, int cx, int cz, CustomPacketPayload pkt) {
        if (world instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, new net.minecraft.world.level.ChunkPos(cx, cz), pkt);
        }
    }

    // TODO Phase 7: Re-implement GUI handling hooks using Neoforge's Container menus and setMenu
    public final void openGui(Player player, int id, BlockPos pos) {
        openGui(player, id, pos.getX(), pos.getY(), pos.getZ());
    }

    public final void openGui(Player player, int guiId) {
        openGui(player, guiId, 0);
    }

    public final void openGui(Player player, int guiId, int entityId) {
        openGui(player, guiId, entityId, 0, 0);
    }

    public final void openGui(Player player, int id, int x, int y, int z) {
        // TODO Phase 7: Re-implement openGui
    }
}
