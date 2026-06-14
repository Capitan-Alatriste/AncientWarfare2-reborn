package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

// TODO Phase 7: import net.shadowmage.ancientwarfare.core.container.ContainerBase;

public record PacketGui(CompoundTag packetData) implements PacketBase {

    public static final CustomPacketPayload.Type<PacketGui> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ancientwarfare", "gui"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketGui> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, PacketGui::packetData,
            PacketGui::new
    );

    public PacketGui() {
        this(new CompoundTag());
    }

    public void setOpenGui(int id, int x, int y, int z) {
        packetData.putBoolean("openGui", true);
        packetData.putInt("id", id);
        packetData.putInt("x", x);
        packetData.putInt("y", y);
        packetData.putInt("z", z);
    }

    public void setTag(String key, CompoundTag tag) {
        packetData.put(key, tag);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void execute(Player player) {
        if (packetData.contains("openGui")) {
            NetworkHandler.INSTANCE.openGui(player, packetData.getInt("id"), packetData.getInt("x"), packetData.getInt("y"), packetData.getInt("z"));
        } else {
            // TODO Phase 7: Re-enable container packet logic
            /*
            if (player.containerMenu instanceof ContainerBase container) {
                container.onPacketData(packetData);
            } else {
                AncientWarfareCore.LOG.error("Invalid target found when processing GUI/Container packet : {} packet: {}", player.containerMenu, packetData);
            }
            */
        }
    }
}
