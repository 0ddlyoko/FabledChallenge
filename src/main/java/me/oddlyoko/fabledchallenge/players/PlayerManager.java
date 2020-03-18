package me.oddlyoko.fabledchallenge.players;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import me.oddlyoko.fabledchallenge.FabledChallenge;
import me.oddlyoko.fabledchallenge.__;
import me.oddlyoko.fabledchallenge.challenges.Challenge;
import me.oddlyoko.fabledchallenge.challenges.Challenge.Type;
import me.oddlyoko.fabledchallenge.challenges.ChallengeCategory;
import me.oddlyoko.fabledchallenge.challenges.Peer;
import me.oddlyoko.fabledchallenge.config.Config;

public class PlayerManager implements Listener {
	private HashMap<UUID, HashMap<Challenge, Integer>> players;
	private File playersDirectory;

	public PlayerManager() {
		players = new HashMap<>();
		playersDirectory = new File("plugins" + File.separator + __.NAME + File.separator + "players");
		playersDirectory.mkdirs();
		Bukkit.getPluginManager().registerEvents(this, FabledChallenge.get());
	}

	public HashMap<Challenge, Integer> getPlayer(UUID uuid) {
		return players.get(uuid);
	}

	/**
	 * Load specific player
	 * 
	 * @param uuid
	 *                 The uuid of specific player
	 */
	private void loadPlayer(UUID uuid) {
		File f = new File(playersDirectory, uuid.toString() + ".yml");
		Config config = new Config(f);
		HashMap<Challenge, Integer> challenges = new HashMap<>();
		List<String> keys = config.getKeys("challenges");
		for (String k : keys) {
			int id = config.getInt("challenges." + k + ".id");
			ChallengeCategory cc = FabledChallenge.get().getChallengeManager().getChallenge(id);
			// WTF
			if (cc == null)
				continue;
			List<String> done = config.getKeys("challenges." + k + ".challenges");
			for (String d : done) {
				String key = "challenges." + k + ".challenges." + d;
				int cId = config.getInt(key + ".id");
				int count = config.getInt(key + ".count");
				Challenge c = cc.getChallenge(cId);
				if (c == null)
					continue;
				challenges.put(c, count);
			}
		}
		players.put(uuid, challenges);
	}

	/**
	 * Unload specific player
	 * 
	 * @param uuid
	 *                 The uuid of specific player
	 */
	private void unloadPlayer(UUID uuid) {
		players.remove(uuid);
	}

	/**
	 * Check if specific player can do specific challenge
	 * 
	 * @param p
	 *              The player
	 * @param c
	 *              The challenge
	 * @return true if specific player can execute specific challenge
	 */
	public boolean canDoChallenge(Player p, Challenge c) {
		if (c == null)
			return false;
		UUID uuid = p.getUniqueId();
		HashMap<Challenge, Integer> done = players.get(uuid);
		if (done == null) {
			// Wtf ?
			loadPlayer(uuid);
			done = players.get(uuid);
		}
		int count = done.getOrDefault(c, 0);
		if (count >= c.getMaxTimes())
			return false;
		// Check if player has required items
		for (Peer<Type, Object> peer : c.getRequires())
			if (!peer.getKey().has(p, peer.getValue()))
				return false;
		return true;
	}

	/**
	 * Perform specific challenge for specific player
	 * 
	 * @param p
	 *              Specific player
	 * @param c
	 *              Specific challenge
	 * @return true if all is good
	 */
	public boolean doChallenge(Player p, Challenge c) {
		if (!canDoChallenge(p, c))
			return false;
		UUID uuid = p.getUniqueId();
		HashMap<Challenge, Integer> done = players.get(uuid);
		int count = done.getOrDefault(c, 0);
		done.put(c, count + 1);
		addChallenge(uuid, c);
		// Take items
		for (Peer<Type, Object> peer : c.getRequires())
			peer.getKey().executeRequire(p, peer.getValue());
		for (Peer<Type, Object> peer : c.getRewards())
			peer.getKey().executeReward(p, peer.getValue());
		// Ok, send message
		if (c.isShowInChat())
			Bukkit.broadcastMessage(FabledChallenge.get().getConfigManager().getChatMessage()
					.replace("{prefix}", __.PREFIX).replace("{player}", p.getName()).replace("{challenge}", c.getName())
					.replace("{amount}", Integer.toString(count + 1))
					.replace("{max}", Integer.toString(c.getMaxTimes())));
		return true;
	}

	public void addChallenge(UUID uuid, Challenge c) {
		File f = new File(playersDirectory, uuid.toString() + ".yml");
		if (!f.exists())
			try {
				f.createNewFile();
			} catch (IOException ignored) {
			}
		Config config = new Config(f);
		int ccId = c.getCategory().getId();
		int cId = c.getId();
		int count = 1;
		if (config.exist("challenges." + ccId + ".challenges." + cId + ".count"))
			count = config.getInt("challenges." + ccId + ".challenges." + cId + ".count") + 1;
		config.set("challenges." + ccId + ".id", ccId);
		config.set("challenges." + ccId + ".challenges." + cId + ".id", cId);
		config.set("challenges." + ccId + ".challenges." + cId + ".count", count);
	}

	/**
	 * Return the number of time specific player has done specific challenge
	 * 
	 * @param uuid
	 *                 The player's uuid
	 * @param c
	 *                 The challenge
	 * @return The number of time specific challenge has been done by player
	 */
	public int getChallengeCount(UUID uuid, Challenge c) {
		File f = new File(playersDirectory, uuid.toString() + ".yml");
		if (!f.exists())
			return 0;
		Config config = new Config(f);
		int ccId = c.getCategory().getId();
		int cId = c.getId();
		if (!config.exist("challenges." + ccId + ".challenges." + cId + ".count"))
			return 0;
		return config.getInt("challenges." + ccId + ".challenges." + cId + ".count");
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		loadPlayer(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		unloadPlayer(e.getPlayer().getUniqueId());
	}
}
