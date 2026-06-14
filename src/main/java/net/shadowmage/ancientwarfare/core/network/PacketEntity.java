package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public record PacketEntity(int entityId, CompoundTag packetData) implements PacketBase {

    public static final CustomPacketPayload.Type<PacketEntity> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ancientwarfare", "entity"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketEntity> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PacketEntity::entityId,
            ByteBufCodecs.COMPOUND_TAG, PacketEntity::packetData,
            PacketEntity::new
    );

    public PacketEntity(Entity e, CompoundTag packetData) {
        this(e.getId(), packetData);
    }

    public PacketEntity(Entity e) {
        this(e.getId(), new CompoundTag());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void execute(Player player) {
        Entity e = player.level().getEntity(entityId);
        // TODO Phase 2: if (e instanceof net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler handler) {
        // TODO Phase 2:     handler.handlePacketData(packetData);
        // TODO Phase 2: }
    }
}
