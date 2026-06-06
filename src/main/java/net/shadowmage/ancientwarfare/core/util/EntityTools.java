package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EntityTools {
	private static final String FACTION_NAME_TAG = "factionName";

	private EntityTools() {
	}

	@Nullable
	public static InteractionHand getHandHoldingItem(LivingEntity entity, Item item) {
		if (entity.getMainHandItem().getItem() == item) {
			return InteractionHand.MAIN_HAND;
		} else if (entity.getOffhandItem().getItem() == item) {
			return InteractionHand.OFF_HAND;
		}
		return null;
	}

	public static ItemStack getItemFromEitherHand(Player player, Class... itemClasses) {
		for (Class itemClass : itemClasses) {
			if (itemClass.isInstance(player.getMainHandItem().getItem())) {
				return player.getMainHandItem();
			} else if (itemClass.isInstance(player.getOffhandItem().getItem())) {
				return player.getOffhandItem();
			}
		}
		return ItemStack.EMPTY;
	}

	public static String getUnlocName(String resourceLocation) {
		return getUnlocName(ResourceLocation.parse(resourceLocation));
	}

	public static String getUnlocName(ResourceLocation registryName) {
		EntityType<?> e = BuiltInRegistries.ENTITY_TYPE.get(registryName);
		return e.getDescriptionId();
	}

	public static <T extends Entity> List<T> getEntitiesWithinBounds(Level world, Class<? extends T> clazz, BlockPos p1, BlockPos p2) {
		AABB bb = new AABB(p1.getX(), p1.getY(), p1.getZ(), p2.getX() + 1, p2.getY() + 1, p2.getZ() + 1);
		return world.getEntitiesOfClass((Class<T>)clazz, bb);
	}

	public static void spawnEntity(Level world, ResourceLocation entityName, CompoundTag entityNBT, BlockPos pos, String... tags) {
        if (!(world instanceof ServerLevel)) return;

		EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(entityName);
		if (type == null) {
			return;
		}

        Entity e = type.create(world);
        if (e == null) return;

		e.moveTo(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d, world.random.nextFloat() * 360, 0);
		if (e instanceof Mob) {
			((Mob) e).finalizeSpawn((ServerLevel)world, world.getCurrentDifficultyAt(e.blockPosition()), MobSpawnType.EVENT, null);
		}
		setDataFromTag(e, entityNBT); //some data needs to be set before spawning entity in the world (like factionName)
		if (tags.length > 0) {
			e.getTags().addAll(Arrays.asList(tags));
		}
		world.addFreshEntity(e);
		setDataFromTag(e, entityNBT); //and some data needs to be set after onInitialSpawn fires for entity]
	}

	private static void setDataFromTag(Entity e, CompoundTag entityNBT) {
		CompoundTag temp = new CompoundTag();
		if (false /* TODO: Phase X NpcFaction */) {
			//((NpcFaction) e).setFactionNameAndDefaults(entityNBT.getString(FACTION_NAME_TAG));
		}
		e.saveWithoutId(temp);
		Set<String> keys = entityNBT.getAllKeys();
		for (String key : keys) {
			temp.put(key, entityNBT.get(key));
		}
		e.load(temp);
	}

	@Nullable
	public static Player findClosestPlayer(Level world, BlockPos pos, int maxSqDistance) {
		return world.players().stream().filter(player -> player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < maxSqDistance).findFirst().orElse(null);
	}
}
