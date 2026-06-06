package net.shadowmage.ancientwarfare.core.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;

public class PacketHelper {
	private PacketHelper() {
	}

	public static void writeTagToBuffer(ByteBuf buffer, CompoundTag tag) {
		try {
			if (tag == null) {
				buffer.writeByte(0);
			} else {
				buffer.writeByte(1);
				NbtIo.writeCompressed(tag, new ByteBufOutputStream(buffer));
			}
		} catch (Exception e) {
			System.err.println("Error writing tag to buffer: " + e.getMessage());
		}
	}

	public static CompoundTag readTagFromBuffer(ByteBuf buffer) {
		try {
			int val = buffer.readByte();
			if (val == 0) {
				return null;
			}
			return NbtIo.readCompressed(new ByteBufInputStream(buffer), NbtAccounter.unlimitedHeap());
		} catch (Exception e) {
			System.err.println("Error reading tag from buffer: " + e.getMessage());
		}
		return null;
	}
}
