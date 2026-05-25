package net.shadowmage.ancientwarfare.core.owner;

import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class ScoreboardTeamViewer implements ITeamViewer {
	@Override
	public boolean areTeamMates(Level level, UUID player1, UUID player2, String playerName1, String playerName2) {
		return player1.equals(player2) || isSameTeam(level, playerName1, playerName2);
	}

	@Override
	public boolean areFriendly(Level level, UUID player1, @Nullable UUID player2, String playerName1, String playerName2) {
		return playerName1.equals(playerName2) || player1.equals(player2) || isSameTeam(level, playerName1, playerName2);
	}

	@Override
	public Set<ResourceLocation> getPlayerTeamNames(Level level, UUID playerId, String playerName) {
		PlayerTeam team = level.getScoreboard().getPlayersTeam(playerName);
		return team == null ? Collections.emptySet() : Collections.singleton(ResourceLocation.tryParse(team.getName()));
	}

	@Override
	public String getName() {
		return "minecraft";
	}

	private boolean isSameTeam(Level level, String playerName1, String playerName2) {
		Team team = level.getScoreboard().getPlayersTeam(playerName1);
		PlayerTeam team2 = level.getScoreboard().getPlayersTeam(playerName2);
		return team != null && team2 != null && team.getName().equals(team2.getName());
	}
}
