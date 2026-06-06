package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.minecraft.core.HolderLookup;

public class Zone implements INBTSerializable<CompoundTag> {

	private BlockPos min;
	private BlockPos max;

	public Zone() {
	}

	public Zone(BlockPos min, BlockPos max) {
		this.min = min;
		this.max = max;
	}

	public BlockPos getMin() {
		return min;
	}

	public BlockPos getMax() {
		return max;
	}

	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider) {
		CompoundTag tag = new CompoundTag();
		if (min != null && max != null) {
			tag.putLong("min", min.asLong());
			tag.putLong("max", max.asLong());
		}
		return tag;
	}

	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
		if (tag.contains("min") && tag.contains("max")) {
			min = BlockPos.of(tag.getLong("min"));
			max = BlockPos.of(tag.getLong("max"));
		}
	}
}
