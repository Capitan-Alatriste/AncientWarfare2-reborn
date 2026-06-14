package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public record PacketResearchUpdate(String playerName, String toAdd, boolean add, boolean live) implements PacketBase {

    public static final CustomPacketPayload.Type<PacketResearchUpdate> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ancientwarfare", "research_update"));

    public static final StreamCodec<FriendlyByteBuf, PacketResearchUpdate> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PacketResearchUpdate::playerName,
            ByteBufCodecs.STRING_UTF8, PacketResearchUpdate::toAdd,
            ByteBufCodecs.BOOL, PacketResearchUpdate::add,
            ByteBufCodecs.BOOL, PacketResearchUpdate::live,
            PacketResearchUpdate::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void execute(Player player) {
        if (live) {
            if (add) {
                ResearchTracker.INSTANCE.addResearch(player.level(), playerName, toAdd);
            }
        } else {
            if (add) {
                ResearchTracker.INSTANCE.addQueuedGoal(player.level(), playerName, toAdd);
            } else {
                ResearchTracker.INSTANCE.removeQueuedGoal(player.level(), playerName, toAdd);
            }
        }
    }
}
