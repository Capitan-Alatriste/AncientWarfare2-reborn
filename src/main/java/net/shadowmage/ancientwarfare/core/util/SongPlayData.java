package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class SongPlayData {
	public static final String AUTO_LOAD_PREFIX = "auto_loaded.";
	public String songName;
	public List<SongEntry> entries = new ArrayList<>();

	public void readFromNBT(CompoundTag tag) {
		this.songName = tag.getString("songName");
		ListTag list = tag.getList("entries", 10);
		for (int i = 0; i < list.size(); i++) {
			SongEntry e = new SongEntry();
			e.readFromNBT(list.getCompound(i));
			entries.add(e);
		}
	}

	public CompoundTag writeToNBT(CompoundTag tag) {
		tag.putString("songName", songName);
		ListTag list = new ListTag();
		for (SongEntry entry : entries) {
			list.add(entry.writeToNBT(new CompoundTag()));
		}
		tag.put("entries", list);
		return tag;
	}

	public int getLength() {
		if (entries.isEmpty()) {
			return 0;
		}
		int length = 0;
		for (SongEntry e : entries) {
			if (e.tick > length) {
				length = e.tick;
			}
		}
		return length;
	}

	public static class SongEntry {
		public ResourceLocation soundRegistryName;
		public float pitch;
		public float volume;
		public int tick;

		public void readFromNBT(CompoundTag tag) {
			String soundName = tag.getString("sound");
			if (soundName.indexOf(':') < 0) {
				soundName = "minecraft:" + soundName;
			}
			this.soundRegistryName = ResourceLocation.parse(soundName);
			this.pitch = tag.getFloat("pitch");
			this.volume = tag.getFloat("volume");
			this.tick = tag.getInt("tick");
		}

		public CompoundTag writeToNBT(CompoundTag tag) {
			tag.putString("sound", soundRegistryName.toString());
			tag.putFloat("pitch", pitch);
			tag.putFloat("volume", volume);
			tag.putInt("tick", tick);
			return tag;
		}

		@OnlyIn(Dist.CLIENT)
		public SoundEvent getSoundEvent() {
			SoundEvent event = BuiltInRegistries.SOUND_EVENT.get(soundRegistryName);
			if (event == null) {
				event = tryFindReplacementSound(soundRegistryName);
			}
			return event;
		}

		private SoundEvent tryFindReplacementSound(ResourceLocation soundRegistryName) {
			if (!soundRegistryName.getNamespace().startsWith("ancientwarfare") || !soundRegistryName.getPath().startsWith(AUTO_LOAD_PREFIX)) {
				return null;
			}

			String soundDomain = soundRegistryName.getNamespace();
			String soundName = soundRegistryName.getPath().substring(AUTO_LOAD_PREFIX.length());

			for (SoundEvent soundEvent : BuiltInRegistries.SOUND_EVENT) {
				ResourceLocation soundEventRegistryName = BuiltInRegistries.SOUND_EVENT.getKey(soundEvent);
				if (soundEventRegistryName == null) continue;
				String resPath = soundEventRegistryName.getPath();
				if (soundEventRegistryName.getNamespace().equals(soundDomain)
						&& resPath.startsWith(AUTO_LOAD_PREFIX)
						&& resPath.substring(AUTO_LOAD_PREFIX.length()).equals(soundName)) {

					SoundEvent found = BuiltInRegistries.SOUND_EVENT.get(soundEventRegistryName);
					System.out.println("Sound " + soundRegistryName + " replaced with automatically found " + found.getLocation());
					this.soundRegistryName = soundEventRegistryName;
					return found;
				}
			}
			System.err.println("Sound " + soundRegistryName + " no longer exists in the sound registry and no replacement was automatically found.");
			return null;
		}

		public static SongEntry create(SoundEvent evt, float p, float v, int t) {
			SongEntry e = new SongEntry();
			e.soundRegistryName = BuiltInRegistries.SOUND_EVENT.getKey(evt);
			e.pitch = p;
			e.volume = v;
			e.tick = t;
			return e;
		}
	}
}
