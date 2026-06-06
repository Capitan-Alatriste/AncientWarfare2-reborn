package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.CompoundTag;

public class NBTBuilder {

	private final CompoundTag tag;

	public NBTBuilder() {
		tag = new CompoundTag();
	}

	public NBTBuilder(CompoundTag tag) {
		this.tag = tag;
	}

	public CompoundTag getTag() {
		return tag;
	}

	public NBTBuilder setInt(String name, int value) {
		tag.putInt(name, value);
		return this;
	}

	public NBTBuilder setFloat(String name, float value) {
		tag.putFloat(name, value);
		return this;
	}

	public NBTBuilder setDouble(String name, double value) {
		tag.putDouble(name, value);
		return this;
	}

	public NBTBuilder setString(String name, String value) {
		tag.putString(name, value);
		return this;
	}

	public NBTBuilder setBoolean(String name, boolean value) {
		tag.putBoolean(name, value);
		return this;
	}

}
