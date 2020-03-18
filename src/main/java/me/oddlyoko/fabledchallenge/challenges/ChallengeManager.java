package me.oddlyoko.fabledchallenge.challenges;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.oddlyoko.fabledchallenge.FabledChallenge;
import me.oddlyoko.fabledchallenge.__;
import me.oddlyoko.fabledchallenge.config.Config;

public class ChallengeManager {
	private Config config;
	private HashMap<Integer, ChallengeCategory> categories;

	public ChallengeManager() {
		File f = new File("plugins" + File.separator + "FabledChallenge" + File.separator + "challenges.yml");
		if (!f.exists()) {
			f.getParentFile().mkdirs();
			try {
				f.createNewFile();
			} catch (IOException ignored) {
			}
		}
		config = new Config(f);
		categories = new HashMap<>();
		loadChallenges();
	}

	private void loadChallenges() {
		Bukkit.getLogger().info(__.PREFIX + ChatColor.GREEN + " Loading categories ...");
		List<String> keys = config.getKeys("challenges");
		try {
			for (String k : keys) {
				int id = config.getInt("challenges." + k + ".id");
				String name = config.getString("challenges." + k + ".name");
				ChallengeCategory cc = new ChallengeCategory(id, name, config);
				categories.put(id, cc);
			}
		} catch (IllegalArgumentException ex) {
			Bukkit.getLogger().log(Level.SEVERE, "Error while loading challenges:", ex);
			Bukkit.getPluginManager().disablePlugin(FabledChallenge.get());
			return;
		}
		Bukkit.getLogger().info(__.PREFIX + ChatColor.GREEN + " challenges loaded with " + ChatColor.GOLD
				+ categories.size() + ChatColor.GREEN + " categories");
	}

	public ChallengeCategory getChallenge(int id) {
		return categories.get(id);
	}
}
