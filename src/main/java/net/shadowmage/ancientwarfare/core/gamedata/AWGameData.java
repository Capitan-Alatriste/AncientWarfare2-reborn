package net.shadowmage.ancientwarfare.core.gamedata;

import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

/*
 * Helps building specific world data.
 */
public final class AWGameData {

	public static final AWGameData INSTANCE = new AWGameData();

	public <T extends SavedData> T getData(Level world, Class<T> clz) {
		if (!(world instanceof ServerLevel)) {
			throw new IllegalStateException("Unable to get SavedData on client side");
		}
		return initData(((ServerLevel)world).getDataStorage(), clz);
	}

	public <T extends SavedData> T getPerWorldData(Level world, Class<T> clz) {
		return getData(world, clz); // DimensionDataStorage handles all dimensions in modern MC
	}

	private <T extends SavedData> T initData(DimensionDataStorage storage, Class<T> clz) {
		String name = "AW" + clz.getSimpleName();
		return storage.computeIfAbsent(new SavedData.Factory<>(() -> {
			try {
				return clz.getConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Error instantiating SavedData", e);
			}
		}, (tag, provider) -> {
			try {
				T inst = clz.getConstructor().newInstance();
				// Dynamic reflection load for WorldData since we don't have direct access here easily
				if (inst instanceof WorldData) {
					return (T) WorldData.load(tag, provider);
				}
				return inst;
			} catch (Exception e) {
				throw new RuntimeException("Error loading SavedData", e);
			}
		}, null), name);
	}
}
