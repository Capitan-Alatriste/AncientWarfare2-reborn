package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public record PacketResearchStart(String playerName, String toAdd, boolean start) implements PacketBase {

    public static final CustomPacketPayload.Type<PacketResearchStart> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ancientwarfare", "research_start"));

    public static final StreamCodec<FriendlyByteBuf, PacketResearchStart> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, PacketResearchStart::playerName,
            ByteBufCodecs.STRING_UTF8, PacketResearchStart::toAdd,
            ByteBufCodecs.BOOL, PacketResearchStart::start,
            PacketResearchStart::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void execute(Player player) {
        if (start) {
            ResearchTracker.INSTANCE.startResearch(player.level(), playerName, toAdd);
        } else {
            ResearchTracker.INSTANCE.finishResearch(player.level(), playerName, toAdd);
        }
    }
}
