package me.oddlyoko.fabledchallenge.defaultinv;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import me.oddlyoko.fabledchallenge.config.Config;

public class DefaultInventory {
	private final Item defaultItem = new Item(new ItemStack(Material.AIR));
	@Getter
	private int size;
	private Item[][] items;

	public DefaultInventory(Config config) {
		size = config.getInt("inventory.size");
		items = new Item[9][size];
		for (String key : config.getKeys("inventory.items")) {
			String k = "inventory.items." + key;
			int row = config.getInt(k + ".row");
			int col = config.getInt(k + ".col");
			String strItem = config.getString(k + ".item");
			int amount = config.getInt(k + ".amount");
			String name = config.getString(k + ".name");
			List<String> lore = config.getStringList(k + ".lore");
			int redirect = config.getInt(k + ".redirect");
			Material item = Material.matchMaterial(strItem);
			if (item == null || item == Material.AIR) {
				Bukkit.getLogger().warning("Item " + strItem + " is not a Material");
				continue;
			}

			ItemStack is = new ItemStack(item, amount);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(name);
			im.setLore(lore);
			is.setItemMeta(im);
			items[col - 1][row - 1] = new Item(is, redirect);
		}
	}

	public Item get(int row, int col) {
		Item is = items[col][row];
		if (is == null)
			is = defaultItem;
		// Clone it
		return new Item(is.getItemstack().clone(), is.getRedirect());
	}
}
