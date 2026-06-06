package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class RayTraceUtils {

	@Nullable
	public static HitResult getPlayerTarget(Player player, float range, float border) {
		HashSet<Entity> excluded = new HashSet<>();
		excluded.add(player);
		if (player.getVehicle() != null) {
			excluded.add(player.getVehicle());
		}
		float yOffset = player.getEyeHeight();
		Vec3 look = player.getViewVector(1.0F);
		look = look.scale(range);
		look = look.add(player.getX(), player.getY() + yOffset, player.getZ());
		return tracePath(player.level(), player.getX(), player.getY() + yOffset, player.getZ(), look.x, look.y, look.z, border, excluded);
	}

	@Nullable
	public static HitResult tracePathWithYawPitch(Level world, float x, float y, float z, float yaw, float pitch, float range, float borderSize, HashSet<Entity> excluded) {
		float tx = x + (Trig.sinDegrees(yaw + 180) * range * Trig.cosDegrees(pitch));
		float ty = (-Trig.sinDegrees(pitch) * range) + y;
		float tz = z + (Trig.cosDegrees(yaw) * range * Trig.cosDegrees(pitch));
		return tracePath(world, x, y, z, tx, ty, tz, borderSize, excluded);
	}

	@Nullable
    public static HitResult tracePath(Level world, double x, double y, double z, double tx, double ty, double tz, float borderSize, HashSet<Entity> excluded) {
		double minX = x < tx ? x : tx;
		double minY = y < ty ? y : ty;
		double minZ = z < tz ? z : tz;
		double maxX = x > tx ? x : tx;
		double maxY = y > ty ? y : ty;
		double maxZ = z > tz ? z : tz;
		AABB bb = new AABB(minX, minY, minZ, maxX, maxY, maxZ).inflate(borderSize, borderSize, borderSize);
		List<Entity> allEntities = world.getEntitiesOfClass(Entity.class, bb, e -> true);
		Entity closestHitEntity = null;
		float closestHit = Float.POSITIVE_INFINITY;
		float currentHit;
		Vec3 startVec = new Vec3(x, y, z);
		Vec3 endVec = new Vec3(tx, ty, tz);
		for (Entity ent : allEntities) {
			if (ent.isPickable() && !excluded.contains(ent)) {
				AABB entityBb = ent.getBoundingBox();
				if (entityBb != null) {
					float entBorder = ent.getPickRadius();
					Optional<Vec3> intercept = entityBb.inflate(entBorder, entBorder, entBorder).clip(startVec, endVec);
					if (intercept.isPresent()) {
						currentHit = (float) intercept.get().distanceTo(startVec);
						if (currentHit < closestHit || currentHit == 0) {
							closestHit = currentHit;
							closestHitEntity = ent;
						}
					}
				}
			}
		}
		if (closestHitEntity != null) {
			return new EntityHitResult(closestHitEntity);
		}
		startVec = new Vec3(x, y, z);
		endVec = new Vec3(tx, ty, tz);
		return world.clip(new ClipContext(startVec, endVec, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, net.minecraft.world.phys.shapes.CollisionContext.empty()));
	}

	@Nullable
	public static HitResult raytraceMultiAABB(List<AABB> aabbs, BlockPos pos, Vec3 start, Vec3 end) {
		List<HitResult> list = new ArrayList<>();

		for (AABB axisalignedbb : aabbs) {
			list.add(rayTraceAABBIntercept(pos, start, end, axisalignedbb));
		}

		HitResult ret = null;
		double closestHit = Integer.MAX_VALUE;

		for (HitResult raytraceresult : list) {
			if (raytraceresult != null) {
				double distance = raytraceresult.getLocation().distanceToSqr(start);

				if (distance < closestHit) {
					ret = raytraceresult;
					closestHit = distance;
				}
			}
		}

		return ret;
	}

	@Nullable
	private static BlockHitResult rayTraceAABBIntercept(BlockPos pos, Vec3 start, Vec3 end, AABB boundingBox) {
		Vec3 vecA = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		Vec3 vecB = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		Optional<Vec3> raytraceresult = boundingBox.clip(vecA, vecB);
		return raytraceresult.map(vec3 -> new BlockHitResult(vec3.add(pos.getX(), pos.getY(), pos.getZ()), net.minecraft.core.Direction.UP, pos, false)).orElse(null);
	}

    public interface Function2<T, U, R> {
        R apply(T t, U u);
    }

    @Nullable
	public static <T> T raytraceMultiAABB(List<AABB> aabbs, BlockPos pos, Vec3 start, Vec3 end, Function2<HitResult, AABB, T> getValue) {
		List<HitResult> list = new ArrayList<>();

		for (AABB axisalignedbb : aabbs) {
			list.add(rayTraceAABBIntercept(pos, start, end, axisalignedbb));
		}

		T ret = null;
		double closestHit = Integer.MAX_VALUE;

		for (int i = 0; i < list.size(); i++) {
			HitResult raytraceresult = list.get(i);
			if (raytraceresult != null) {
				double distance = raytraceresult.getLocation().distanceToSqr(start);

				if (distance < closestHit) {
					ret = getValue.apply(raytraceresult, aabbs.get(i));
					closestHit = distance;
				}
			}
		}

		return ret;
	}

}
