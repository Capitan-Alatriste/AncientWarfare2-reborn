package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class NBTBuilder {
	private NBTTagCompound tag = new NBTTagCompound();

	public NBTBuilder setString(String key, String value) {
		tag.putString(key, value);
		return this;
	}

	public NBTBuilder setBoolean(String key, boolean value) {
		tag.putBoolean(key, value);
		return this;
	}

	public NBTTagCompound build() {
		return tag;
	}

	public NBTBuilder setByte(String key, int value) {
		tag.putByte(key, (byte) value);
		return this;
	}

	public NBTBuilder setUniqueId(String key, UUID uniqueId) {
		tag.putUUID(key, uniqueId);
		return this;
	}

	public NBTBuilder setLong(String key, long value) {
		tag.putLong(key, value);
		return this;
	}

	public NBTBuilder setInteger(String key, int value) {
		tag.putInt(key, value);
		return this;
	}

	public NBTBuilder setTag(String key, NBTTagCompound tagCompound) {
		tag.put(key, tagCompound);
		return this;
	}
}
