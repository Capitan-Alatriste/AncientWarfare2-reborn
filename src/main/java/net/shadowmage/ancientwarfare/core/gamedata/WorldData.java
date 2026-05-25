package net.shadowmage.ancientwarfare.core.gamedata;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.core.HolderLookup;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class WorldData extends SavedData {
	CompoundTag dataTag = new CompoundTag();
	private Set<UUID> playersGivenManual = new HashSet<>();

	public WorldData() {
	}

	public final boolean get(String key) {
		return dataTag.getBoolean(key);
	}

	public final void set(String name, boolean val) {
		dataTag.putBoolean(name, val);
		setDirty();
	}

	public final void addPlayerThatWasGivenManual(Player player) {
		playersGivenManual.add(player.getUUID());
		setDirty();
	}

	public boolean wasPlayerGivenManual(Player player) {
		return playersGivenManual.contains(player.getUUID());
	}

	public static WorldData load(CompoundTag tag, HolderLookup.Provider provider) {
		WorldData data = new WorldData();
		data.dataTag = tag.getCompound("AWWorldData");
		if (data.dataTag.contains("playersGivenManual", Tag.TAG_LIST)) {
			ListTag list = data.dataTag.getList("playersGivenManual", Tag.TAG_INT_ARRAY);
			for (int i = 0; i < list.size(); i++) {
				data.playersGivenManual.add(NbtUtils.loadUUID(list.get(i)));
			}
		}
		return data;
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
		ListTag list = new ListTag();
		for (UUID uuid : playersGivenManual) {
			list.add(NbtUtils.createUUID(uuid));
		}
		dataTag.put("playersGivenManual", list);
		tag.put("AWWorldData", this.dataTag);
		return tag;
	}
}
