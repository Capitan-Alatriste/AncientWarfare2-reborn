package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.world.phys.Vec3;

public class Trig {
	private Trig() {
	}

	public static float sinDegrees(float degrees) {
		return (float) Math.sin(Math.toRadians(degrees));
	}

	public static float cosDegrees(float degrees) {
		return (float) Math.cos(Math.toRadians(degrees));
	}

	public static float yawFromVec(Vec3 v) {
		return (float) Math.toDegrees(Math.atan2(-v.x, v.z));
	}

	public static float pitchFromVec(Vec3 v) {
		return (float) Math.toDegrees(Math.atan2(v.y, Math.sqrt(v.x * v.x + v.z * v.z)));
	}

	public static float min(float... vals) {
		float m = vals[0];
		for (float val : vals) {
			if (val < m) {
				m = val;
			}
		}
		return m;
	}

	public static float max(float... vals) {
		float m = vals[0];
		for (float val : vals) {
			if (val > m) {
				m = val;
			}
		}
		return m;
	}
}
