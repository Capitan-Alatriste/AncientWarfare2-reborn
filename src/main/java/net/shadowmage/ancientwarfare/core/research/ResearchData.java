package net.shadowmage.ancientwarfare.core.research;

import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import org.apache.commons.lang3.StringUtils;


import net.shadowmage.ancientwarfare.core.datafixes.ResearchEntryIdNameFixer;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ResearchData extends net.minecraft.world.level.saveddata.SavedData {
	// Stub fixed

	private HashMap<String, ResearchEntry> playerResearchEntries = new HashMap<>();

	public ResearchData() {

	}

	public void onPlayerLogin(Player player) {
		if (!playerResearchEntries.containsKey(player.getName())) {
			playerResearchEntries.put(player.getName().getString(), new ResearchEntry());
			this.setDirty();
		}
	}

	public void readFromNBT(CompoundTag tag) {
		playerResearchEntries.clear();

		ListTag entryList = tag.getList("entryList", net.minecraft.nbt.Tag.TAG_COMPOUND);

		ResearchEntry entry;
		CompoundTag entryTag;
		String name;
		for (int i = 0; i < entryList.size(); i++) {
			entry = new ResearchEntry();
			entryTag = entryList.getCompound(i);
			name = entryTag.getString("playerName");
			entry.readFromNBT(entryTag);
			playerResearchEntries.put(name, entry);
		}
	}

	@Override
	public CompoundTag save(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
		ListTag entryList = new ListTag();
		ResearchEntry entry;

		CompoundTag entryTag;
		for (String name : this.playerResearchEntries.keySet()) {
			entry = this.playerResearchEntries.get(name);
			entryTag = new CompoundTag();
			entryTag.putString("playerName", name);
			entry.writeToNBT(entryTag);
			entryList.add(entryTag);
		}
		tag.put("entryList", entryList);
		return tag;
	}

	public void removeResearchFrom(String playerName, String research) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).removeResearch(research);
			setDirty();
		}
	}

	public void clearResearchFor(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).clearResearch();
			setDirty();
		}
	}

	public void fillResearchFor(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).fillResearch();
			setDirty();
		}
	}

	public Set<String> getResearchableGoals(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			ResearchEntry entry = playerResearchEntries.get(playerName);
			return getResearchableGoalsFor(entry);
		}
		return Collections.emptySet();
	}

	private static Set<String> getResearchableGoalsFor(ResearchEntry researchEntry) {
		Set<String> totalKnowledge = new HashSet<>();
		totalKnowledge.addAll(researchEntry.getCompletedResearch());
		totalKnowledge.addAll(researchEntry.getQueuedResearch());
		Optional<String> inProgress = researchEntry.getCurrentResearch();
		inProgress.ifPresent(totalKnowledge::add);
		Set<String> researchableGoals = new HashSet<>();
		for (ResearchGoal goal : ResearchRegistry.getAllResearchGoals()) {
			if (totalKnowledge.contains(goal.getName())) {
				continue;
			}

			if (goal.canResearch(totalKnowledge)) {
				researchableGoals.add(goal.getName());
			}
		}
		return researchableGoals;
	}

	public Set<String> getResearchFor(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			return playerResearchEntries.get(playerName).getCompletedResearch();
		}
		return Collections.emptySet();
	}

	public void addResearchTo(String playerName, String research) {
		if (!playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.put(playerName, new ResearchEntry());
		}
		this.playerResearchEntries.get(playerName).addResearch(research);
		setDirty();
	}

	public boolean hasPlayerCompletedResearch(String playerName, String research) {
		return playerResearchEntries.containsKey(playerName) && playerResearchEntries.get(playerName).knowsResearch(research);
	}

	public Optional<String> getInProgressResearch(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			return playerResearchEntries.get(playerName).getCurrentResearch();
		}
		return Optional.empty();
	}

	public int getResearchProgress(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			return playerResearchEntries.get(playerName).getResearchProgress();
		}
		return 0;
	}

	public void startResearch(String playerName, String goal) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).startResearch(goal);
			setDirty();
		}
	}

	public void finishResearch(String playerName, String goal) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).finishResearch(goal);
			setDirty();
		}
	}

	public void setCurrentResearchProgress(String playerName, int progress) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).setResearchProgress(progress);
			setDirty();
		}
	}

	public void addQueuedResearch(String playerName, String goal) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).addQueuedResearch(goal);
			setDirty();
		}
	}

	public void removeQueuedResearch(String playerName, String goal) {
		if (playerResearchEntries.containsKey(playerName)) {
			playerResearchEntries.get(playerName).removeQueuedResearch(goal);
			setDirty();
		}
	}

	public List<String> getQueuedResearch(String playerName) {
		if (playerResearchEntries.containsKey(playerName)) {
			return playerResearchEntries.get(playerName).getResearchQueue();
		}
		return Collections.emptyList();
	}

	public boolean addProgress(String playerName, int amount) {
		boolean ret = false;
		if (playerResearchEntries.containsKey(playerName)) {
			ret = playerResearchEntries.get(playerName).addProgress(amount);
			setDirty();
		}

		return ret;
	}

	public boolean hasResearchStarted(String playerName) {
		return playerResearchEntries.containsKey(playerName) && playerResearchEntries.get(playerName).hasResearchStarted();
	}

	public static final class ResearchEntry {
		private String currentResearch = null;
		private int currentProgress = -1;
		private Set<String> completedResearch = new HashSet<>();
		private List<String> queuedResearch = new ArrayList<>();

		private boolean knowsResearch(String researchName) {
			return getCompletedResearch().contains(researchName);
		}

		public Optional<String> getCurrentResearch() {
			return Optional.ofNullable(currentResearch);
		}

		private void resetCurrentResearch() {
			currentResearch = null;
		}

		public void setCurrentResearch(String currentResearch) {
			if (StringUtils.isEmpty(currentResearch)) {
				return;
			}
			this.currentResearch = currentResearch;
		}

		public boolean addProgress(int amount) {
			Optional<String> curResearch = getCurrentResearch();
			if (curResearch.isPresent()) {
				currentProgress += amount;
				if (currentProgress >= ResearchRegistry.getResearch(curResearch.get()).getTotalResearchTime()) {
					finishResearch(curResearch.get());
				}
				return true;
			}
			return false;
		}

		public void finishResearch(String researchName) {
			if (getCurrentResearch().map(r -> r.equals(researchName)).orElse(false)) {
				getCompletedResearch().add(researchName);
				currentProgress = -1;
				resetCurrentResearch();
			}
		}

		/*
		 * should only be called after a goal from the queue has sucessfully been started -- items used/etc
		 */
		public void startResearch(String goal) {
			if (getCurrentResearch().isPresent() || !queuedResearch.contains(goal)) {
				return;
			}
			queuedResearch.remove(goal);
			setCurrentResearch(goal);
			currentProgress = 0;
		}

		public boolean hasResearchStarted() {
			return currentProgress >= 0 && getCurrentResearch().isPresent();
		}

		private void setResearchProgress(int progress) {
			this.currentProgress = progress;
		}

		private int getResearchProgress() {
			return currentProgress;
		}

		private void addResearch(String researchName) {
			getCompletedResearch().add(researchName);
			if (queuedResearch.contains(researchName)) {
				queuedResearch.remove(researchName);
			}
			if (getCurrentResearch().map(r -> r.equals(researchName)).orElse(false)) {
				resetCurrentResearch();
				currentProgress = -1;
			}
		}

		private void removeResearch(String researchName) {
			this.getCompletedResearch().remove(researchName);
		}

		private void clearResearch() {
			getCompletedResearch().clear();
			currentProgress = -1;
			resetCurrentResearch();
			queuedResearch.clear();
		}

		private void fillResearch() {
			getCompletedResearch().clear();
			currentProgress = -1;
			resetCurrentResearch();
			queuedResearch.clear();
			for (ResearchGoal g : ResearchRegistry.getAllResearchGoals()) {
				getCompletedResearch().add(g.getName());
			}
		}

		private void addQueuedResearch(String researchName) {
			if (!queuedResearch.contains(researchName)) {
				queuedResearch.add(researchName);
			}
		}

		private List<String> getResearchQueue() {
			return queuedResearch;
		}

		private void writeToNBT(CompoundTag tag) {
			if (currentResearch != null) {
				tag.putString("currentResearch", currentResearch);
			}
			tag.putInt("currentProgress", currentProgress);
			ListTag listC = new ListTag();
			for(String s : getCompletedResearch()) listC.add(net.minecraft.nbt.StringTag.valueOf(s));
			tag.put("completedResearch", listC);
			ListTag listQ = new ListTag();
			for(String s : queuedResearch) listQ.add(net.minecraft.nbt.StringTag.valueOf(s));
			tag.put("queuedResearch", listQ);
		}

		private void readFromNBT(CompoundTag tag) {
			CompoundTag fixedTag = tag; // TODO Phase 1: ResearchEntryIdNameFixer.fix(tag)
			removeInvalidEntries(fixedTag);
			if (fixedTag.contains("currentResearch")) {
				currentResearch = fixedTag.getString("currentResearch");
			}
			currentProgress = fixedTag.getInt("currentProgress");
			ListTag listC = fixedTag.getList("completedResearch", net.minecraft.nbt.Tag.TAG_STRING);
			for(int i = 0; i < listC.size(); i++) getCompletedResearch().add(listC.getString(i));
			ListTag listQ = fixedTag.getList("queuedResearch", net.minecraft.nbt.Tag.TAG_STRING);
			for(int i = 0; i < listQ.size(); i++) queuedResearch.add(listQ.getString(i));
		}

		private void removeInvalidEntries(CompoundTag tag) {
			if (tag.contains("currentResearch") && !ResearchRegistry.researchExists(tag.getString("currentResearch"))) {
				tag.remove("currentResearch");
			}
			removeInvalidEntriesFromList(tag, "completedResearch");
			removeInvalidEntriesFromList(tag, "queuedResearch");
		}

		private void removeInvalidEntriesFromList(CompoundTag tag, String listName) {
			ListTag researchList = tag.getList(listName, net.minecraft.nbt.Tag.TAG_STRING);
			Iterator<net.minecraft.nbt.Tag> it = researchList.iterator();

			while (it.hasNext()) {
				String name = it.next().getAsString();
				if (!ResearchRegistry.researchExists(name)) {
					it.remove();
				}
			}
			tag.put(listName, researchList);
		}

		private void removeQueuedResearch(String goal) {
			if (!queuedResearch.contains(goal)) {
				return;
			}

			List<String> goalsToValidate = new ArrayList<>();

			Iterator<String> it = queuedResearch.iterator();
			String exam;
			boolean found = false;
			while (it.hasNext() && (exam = it.next()) != null) {
				if (found) {
					goalsToValidate.add(exam);
					it.remove();
				} else if (exam.equals(goal)) {
					found = true;
					it.remove();
				}
			}

			Set<String> totalResearch = new HashSet<>();
			totalResearch.addAll(getCompletedResearch());
			totalResearch.addAll(queuedResearch);
			Optional<String> currentResearch = getCurrentResearch();
			currentResearch.ifPresent(totalResearch::add);

			ResearchGoal g;
			for (String g1 : goalsToValidate) {
				g = ResearchRegistry.getResearch(g1);
				if (g != null && g.canResearch(totalResearch)) {
					totalResearch.add(g1);
					queuedResearch.add(g1);
				}
			}
		}

		public Set<String> getCompletedResearch() {
			return completedResearch;
		}

		public List<String> getQueuedResearch() {
			return queuedResearch;
		}
	}

}
