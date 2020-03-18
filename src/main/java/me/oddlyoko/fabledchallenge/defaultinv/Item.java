package me.oddlyoko.fabledchallenge.defaultinv;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;

@Getter
public class Item {
	private ItemStack itemstack;
	private int redirect;

	public Item(ItemStack itemstack) {
		this(itemstack, 0);
	}

	public Item(ItemStack itemstack, int redirect) {
		this.itemstack = itemstack;
		this.redirect = redirect;
	}
}
