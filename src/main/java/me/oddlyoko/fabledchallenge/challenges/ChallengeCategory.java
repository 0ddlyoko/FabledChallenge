package me.oddlyoko.fabledchallenge.challenges;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import lombok.Getter;
import me.oddlyoko.fabledchallenge.__;
import me.oddlyoko.fabledchallenge.config.Config;

@Getter
public class ChallengeCategory {
	private Config config;
	private int id;
	private String name;
	private HashMap<Integer, Challenge> challenges;

	public ChallengeCategory(int id, String name, Config config) {
		this.id = id;
		this.name = name;
		this.config = config;
		challenges = new HashMap<>();
		loadChallenges();
	}

	private void loadChallenges() {
		Bukkit.getLogger().info(__.PREFIX + ChatColor.GREEN + " Loading category " + name);
		List<String> keys = config.getKeys("challenges." + id + ".challenges");
		for (String k : keys) {
			String key = "challenges." + id + ".challenges." + k;
			int id = config.getInt(key + ".id");
			String name = config.getString(key + ".name");
			List<String> require = config.getStringList(key + ".require");
			List<String> reward = config.getStringList(key + ".reward");
			int maxTimes = config.getInt(key + ".maxtimes");
			boolean showInChat = config.getBoolean(key + ".showInChat");
			// Item
			boolean show = config.getBoolean(key + ".item.show");
			int row = show ? config.getInt(key + ".item.row") : 0;
			int col = show ? config.getInt(key + ".item.col") : 0;
			String strItem = show ? config.getString(key + ".item.item") : "AIR";
			int amount = show ? config.getInt(key + ".item.amount") : 0;
			List<String> lore = show ? config.getStringList(key + ".item.lore") : new ArrayList<>();
			try {
				// If an Exception occurs, we don't handle it here but in parent class
				Material item = Material.matchMaterial(strItem);
				if (item == null)
					throw new IllegalArgumentException("Item " + strItem + " isn't a correct material");
				ItemChallenge ic = new ItemChallenge(show, row, col, item, amount, lore);
				Challenge c = new Challenge(this, id, name, maxTimes, showInChat, require, reward, ic);
				challenges.put(id, c);
			} catch (IllegalArgumentException ex) {
				throw new IllegalArgumentException("Exception at category " + this.name + "(" + this.id
						+ ") at challenge " + name + "(" + id + "): " + ex.getMessage());
			}
		}
		Bukkit.getLogger().info(__.PREFIX + ChatColor.GREEN + " Category " + name + ChatColor.GREEN + " loaded with "
				+ ChatColor.GOLD + challenges.size() + ChatColor.GREEN + " challenges");
	}

	public Challenge getChallenge(int id) {
		return challenges.get(id);
	}
}
