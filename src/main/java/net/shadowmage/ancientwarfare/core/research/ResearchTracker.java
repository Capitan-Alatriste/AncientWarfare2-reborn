package net.shadowmage.ancientwarfare.core.research;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.shadowmage.ancientwarfare.core.gamedata.AWGameData;

// TODO Phase 6: Packet imports


import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public final class ResearchTracker {

	public static final ResearchTracker INSTANCE = new ResearchTracker();
	private final ResearchData clientData;

	private ResearchTracker() {
		clientData = new ResearchData();
	}

	/*
	 * SERVER ONLY
	 */
	@SubscribeEvent
	public void playerLogInEvent(PlayerEvent.PlayerLoggedInEvent evt) {
		getResearchData(evt.getEntity().level()).onPlayerLogin(evt.getEntity());
		// TODO Phase 6: PacketResearchInit

	}

	public void clearResearch(Level world, String playerName) {
		if (world.isClientSide) {
			clientData.clearResearchFor(playerName);
		} else {
			getResearchData(world).clearResearchFor(playerName);
			// TODO Phase 6: PacketResearchInit

		}
	}

	public void removeResearch(Level world, String playerName, String research) {
		if (world.isClientSide) {
			clientData.removeResearchFrom(playerName, research);
		} else {
			getResearchData(world).removeResearchFrom(playerName, research);
			// TODO Phase 6: PacketResearchInit

		}
	}

	public void fillResearch(Level world, String playerName) {
		if (world.isClientSide) {
			clientData.fillResearchFor(playerName);
		} else {
			getResearchData(world).fillResearchFor(playerName);
			// TODO Phase 6: PacketResearchInit

		}
	}

	public void addResearch(Level world, String playerName, String research) {
		if (world.isClientSide) {
			clientData.addResearchTo(playerName, research);
		} else {
			getResearchData(world).addResearchTo(playerName, research);
			// TODO Phase 6: PacketResearchUpdate

		}
	}

	/*
	 * @param world
	 * @param player
	 * @param research
	 * @return
	 */
	public boolean hasPlayerCompleted(Level world, String player, String research) {
		if (world.isClientSide) {
			return clientData.hasPlayerCompletedResearch(player, research);
		}
		return getResearchData(world).hasPlayerCompletedResearch(player, research);
	}

	public boolean addResearchFromNotes(Level world, String player, String research) {
		if (hasPlayerCompleted(world, player, research)) {
			return false;
		}
		addResearch(world, player, research);
		return true;
	}

	public boolean addProgressFromNotes(Level world, String player, String research) {
		if (world.isClientSide) {
			return false;
		}
		ResearchGoal goal = ResearchRegistry.getResearch(research);
		return getResearchData(world).addProgress(player, goal.getTotalResearchTime() / 4);
	}

	/*
	 * @param world
	 * @param playerName
	 * @return
	 */
	public Set<String> getCompletedResearchFor(Level world, String playerName) {
		if (world.isClientSide) {
			return clientData.getResearchFor(playerName);
		}
		return getResearchData(world).getResearchFor(playerName);
	}

	public List<String> getResearchQueueFor(Level world, String playerName) {
		if (world.isClientSide) {
			return Collections.emptyList();
		}
		return getResearchData(world).getQueuedResearch(playerName);
	}

	public Set<String> getResearchableGoals(Level world, String playerName) {
		if (world.isClientSide) {
			return clientData.getResearchableGoals(playerName);
		} else {
			return getResearchData(world).getResearchableGoals(playerName);
		}
	}

	/*
	 * @param world
	 * @return
	 */
	private ResearchData getResearchData(Level world) {
		if (world.isClientSide) {
			return clientData;
		}
		return AWGameData.INSTANCE.getData(world, ResearchData.class);
	}

	/*
	 * CLIENT ONLY
	 */
	public void onClientResearchReceived(CompoundTag researchDataTag) {
		this.clientData.readFromNBT(researchDataTag);
	}

	public Optional<String> getCurrentGoal(Level world, String playerName) {
		if (world.isClientSide) {
			return clientData.getInProgressResearch(playerName);
		}
		return getResearchData(world).getInProgressResearch(playerName);
	}

	public int getProgress(Level world, String playerName) {
		if (world.isClientSide) {
			return clientData.getResearchProgress(playerName);
		}
		return getResearchData(world).getResearchProgress(playerName);
	}

	public void setProgress(Level world, String playerName, int progress) {
		if (world.isClientSide) {
			clientData.setCurrentResearchProgress(playerName, progress);
		} else {
			getResearchData(world).setCurrentResearchProgress(playerName, progress);
		}
	}

	public void removeQueuedGoal(Level world, String playerName, String goal) {
		if (world.isClientSide) {
			clientData.removeQueuedResearch(playerName, goal);
		} else {
			getResearchData(world).removeQueuedResearch(playerName, goal);
			// TODO Phase 6: PacketResearchUpdate

		}
	}

	public void addQueuedGoal(Level world, String playerName, String goal) {
		if (world.isClientSide) {
			clientData.addQueuedResearch(playerName, goal);
		} else {
			getResearchData(world).addQueuedResearch(playerName, goal);
			// TODO Phase 6: PacketResearchUpdate

		}
	}

	public void startResearch(Level world, String playerName, String goal) {
		if (world.isClientSide) {
			clientData.startResearch(playerName, goal);
		} else {
			getResearchData(world).startResearch(playerName, goal);
			// TODO Phase 6: PacketResearchStart

		}
	}

	public void finishResearch(Level world, String playerName, String goal) {
		if (world.isClientSide) {
			clientData.finishResearch(playerName, goal);
		} else {
			getResearchData(world).finishResearch(playerName, goal);
			// TODO Phase 6: PacketResearchStart

		}
	}

}
