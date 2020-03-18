package me.oddlyoko.fabledchallenge.challenges;

import java.util.List;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Getter;
import lombok.Setter;
import me.oddlyoko.fabledchallenge.FabledChallenge;

@Getter
public class ItemChallenge {
	@Setter
	private Challenge challenge;
	private boolean show;
	private int row;
	private int col;
	private Material type;
	private int amount;
	private List<String> lore;

	public ItemChallenge(boolean show, int row, int col, Material type, int amount, List<String> lore) {
		this.show = show;
		this.row = row;
		this.col = col;
		this.type = type;
		this.amount = amount;
		this.lore = lore;
	}

	public ItemStack createItem(UUID player, int amount) {
		ItemStack is = new ItemStack(type, this.amount);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(challenge.getName() + " " + FabledChallenge.get().getConfigManager().getItemLorePlayer()
				.replace("{amount}", Integer.toString(amount))
				.replace("{max}", Integer.toString(challenge.getMaxTimes())));
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}
}
