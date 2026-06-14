package net.shadowmage.ancientwarfare.core.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;


public record PacketBlockEvent(BlockPos pos, short id, short a, short b) implements PacketBase {

    public static final CustomPacketPayload.Type<PacketBlockEvent> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ancientwarfare", "block_event"));

    public static final StreamCodec<FriendlyByteBuf, PacketBlockEvent> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, PacketBlockEvent::pos,
            ByteBufCodecs.SHORT, PacketBlockEvent::id,
            ByteBufCodecs.SHORT, PacketBlockEvent::a,
            ByteBufCodecs.SHORT, PacketBlockEvent::b,
            PacketBlockEvent::new
    );

    public PacketBlockEvent(BlockPos pos, Block block, short a, short b) {
        this(pos, (short) BuiltInRegistries.BLOCK.getId(block), a, b);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void execute(Player player) {
        @SuppressWarnings("deprecation")
        Block block = BuiltInRegistries.BLOCK.byId(id);
        if (block != null) {
            player.level().blockEvent(pos, block, a, b);
        }
    }
}
