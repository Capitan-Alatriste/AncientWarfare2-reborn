package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import net.shadowmage.ancientwarfare.core.research.ResearchData;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;

public record PacketResearchInit(CompoundTag researchDataTag) implements PacketBase {

    public static final CustomPacketPayload.Type<PacketResearchInit> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ancientwarfare", "research_init"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketResearchInit> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, PacketResearchInit::researchDataTag,
            PacketResearchInit::new
    );

    public PacketResearchInit(ResearchData data) {
        this(createTag(data));
    }

    private static CompoundTag createTag(ResearchData data) {
        CompoundTag tag = new CompoundTag();
        return data.save(tag, null);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void execute(Player player) {
        ResearchTracker.INSTANCE.onClientResearchReceived(researchDataTag);
    }
}
