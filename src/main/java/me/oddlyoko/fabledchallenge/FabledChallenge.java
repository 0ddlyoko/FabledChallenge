package me.oddlyoko.fabledchallenge;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.oddlyoko.fabledchallenge.challenges.Challenge;
import me.oddlyoko.fabledchallenge.challenges.ChallengeCategory;
import me.oddlyoko.fabledchallenge.challenges.ChallengeManager;
import me.oddlyoko.fabledchallenge.config.ConfigManager;
import me.oddlyoko.fabledchallenge.defaultinv.DefaultInventory;
import me.oddlyoko.fabledchallenge.inventory.InventoryManager;
import me.oddlyoko.fabledchallenge.inventory.inv.ChallengeInventory;
import me.oddlyoko.fabledchallenge.players.PlayerManager;

public class FabledChallenge extends JavaPlugin {
	private static FabledChallenge INSTANCE;
	@Getter
	private ChallengeManager challengeManager;
	@Getter
	private PlayerManager playerManager;
	@Getter
	private ConfigManager configManager;
	@Getter
	private InventoryManager inventoryManager;
	@Getter
	private DefaultInventory defaultInventory;
	private ChallengeInventory challengeInventory;

	public FabledChallenge() {
		INSTANCE = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();
		configManager = new ConfigManager();
		defaultInventory = new DefaultInventory(configManager.getConfig());
		challengeManager = new ChallengeManager();
		playerManager = new PlayerManager();
		inventoryManager = new InventoryManager();
		inventoryManager.init();
		challengeInventory = new ChallengeInventory();
	}

	@Override
	public void onDisable() {
		inventoryManager.closeInventories();
	}

	public static FabledChallenge get() {
		return INSTANCE;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!"fabledchallenge".equalsIgnoreCase(command.getLabel()))
			return false;
		if (args.length == 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(__.PREFIX + ChatColor.RED + "You must be a player to execute this command");
				return true;
			}
			Player p = (Player) sender;
			// Open inventory
			openChallengeInventory(p, challengeManager.getChallenge(1));
		} else if ("help".equalsIgnoreCase(args[0])) {
			sender.sendMessage(
					ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW + "]-----------");
			sender.sendMessage(ChatColor.AQUA + "- /c" + ChatColor.YELLOW + " : Open challenge menu");
			sender.sendMessage(ChatColor.AQUA + "- /c info" + ChatColor.YELLOW + " : See informations about plugin");
			sender.sendMessage(
					ChatColor.AQUA + "- /c <challengeGroupId> <challengeId>" + ChatColor.YELLOW + " : Do a challenge");
		} else if ("info".equalsIgnoreCase(args[0])) {
			sender.sendMessage(
					ChatColor.YELLOW + "-----------[" + ChatColor.GOLD + __.NAME + ChatColor.YELLOW + "]-----------");
			sender.sendMessage(ChatColor.AQUA + "Created by 0ddlyoko");
			sender.sendMessage(ChatColor.GREEN + "v" + getDescription().getVersion());
			sender.sendMessage(ChatColor.AQUA + "https://www.0ddlyoko.be");
			sender.sendMessage(ChatColor.AQUA + "https://www.github.com/0ddlyoko");
		} else if (args.length == 2) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(__.PREFIX + ChatColor.RED + "You must be a player to execute this command");
				return true;
			}
			Player p = (Player) sender;
			int ccId = 0;
			int cId = 0;
			try {
				ccId = Integer.parseInt(args[0]);
				cId = Integer.parseInt(args[1]);
			} catch (NumberFormatException ex) {
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
				return true;
			}
			ChallengeCategory cc = challengeManager.getChallenge(ccId);
			if (cc == null) {
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
				return true;
			}
			Challenge c = cc.getChallenge(cId);
			if (c == null) {
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
				return true;
			}
			if (playerManager.doChallenge(p, c)) {
				// Ok
				p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
			} else
				p.playSound(p.getLocation(), Sound.BLOCK_GLASS_BREAK, 1, 1);
		}
		return true;
	}

	public void openChallengeInventory(Player p, ChallengeCategory category) {
		if (category == null)
			return;
		Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
			inventoryManager.openInventory(challengeInventory, p, params -> {
				params.put(ChallengeInventory.CATEGORY, category);
			});
		}, 1);
	}
}
