package net.shadowmage.ancientwarfare.core.owner;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;


import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.util.UUID;

@Immutable
public class Owner {
	public static final Owner EMPTY = new Owner();

	private static final String OWNER_NAME_TAG = "ownerName";
	private static final String OWNER_ID_TAG = "ownerId";
	private final UUID uuid;
	private final String name;

	private Owner() {
		this(new UUID(0, 0), "");
	}

	private Owner(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	public Owner(Player player) {
		this(player.getUUID(), player.getName().getString());
	}

	public Owner(FriendlyByteBuf buffer) {
		this(new UUID(buffer.readLong(), buffer.readLong()), buffer.readUtf());
	}

	public Owner(Level level, String name) {
		Player player = null;
		for(Player p : level.players()) {
			if(p.getName().getString().equals(name)) {
				player = p;
				break;
			}
		}
		uuid = player != null ? player.getUUID() : new UUID(0, 0);
		this.name = name;
	}

	public boolean isOwnerOrSameTeamOrFriend(@Nullable Entity entity) {
		// check our own implementation of the ownable entities
		if (entity instanceof IOwnable) {
			Owner owner = ((IOwnable) entity).getOwner();
			return isOwnerOrSameTeamOrFriend(entity.level(), owner.getUUID(), owner.getName());
		}
		// check if entity implements vanilla interface if the entity is ownable & player is the owner
		if (entity instanceof OwnableEntity && ((OwnableEntity) entity).getOwner() != null) {
			Entity owner = ((OwnableEntity) entity).getOwner();
			return isOwnerOrSameTeamOrFriend(entity.level(), owner.getUUID(), owner.getName().getString());
		}
		return entity != null && isOwnerOrSameTeamOrFriend(entity.level(), entity.getUUID(), entity.getName().getString());
	}

	public boolean isOwnerOrSameTeamOrFriend(Level level, @Nullable UUID playerId, String playerName) {
		return TeamViewerRegistry.areFriendly(level, uuid, playerId, name, playerName);
	}

	public String getName() {
		return name;
	}

	public UUID getUUID() {
		return uuid;
	}

	public void serializeToBuffer(FriendlyByteBuf buffer) {
		buffer.writeLong(uuid.getMostSignificantBits());
		buffer.writeLong(uuid.getLeastSignificantBits());
		buffer.writeUtf(name);
	}

	public CompoundTag serializeToNBT(CompoundTag tag) {
		if (this == EMPTY) {
			return tag;
		}
		tag.putString(OWNER_NAME_TAG, name);
		tag.putUUID(OWNER_ID_TAG, uuid);

		return tag;
	}

	public static Owner deserializeFromNBT(CompoundTag tag) {
		if (tag.contains(OWNER_NAME_TAG)) {
			//noinspection ConstantConditions - NBTTagCompound has getUniqueId marked as Nullable incorrectly
			return new Owner(tag.getUUID(OWNER_ID_TAG), tag.getString(OWNER_NAME_TAG));
		}
		return Owner.EMPTY;
	}

	public boolean playerHasCommandPermissions(Level level, UUID playerId, String playerName) {
		return this != Owner.EMPTY && TeamViewerRegistry.areTeamMates(level, uuid, playerId, name, playerName);
	}

}
