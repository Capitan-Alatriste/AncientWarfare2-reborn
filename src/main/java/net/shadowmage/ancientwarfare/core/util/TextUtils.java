package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.core.BlockPos;

public class TextUtils {
	private TextUtils() {
	}

	public static String getString(BlockPos pos) {
		return pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
	}

	public static String padNumberLeft(int value, int padding) {
		return String.format("%0" + padding + "d", value);
	}

	public static String stripPunctuation(String text) {
		return text.replaceAll("\\p{P}", "");
	}

    // TODO Phase X: Method depending on missing config/automation classes.
}
