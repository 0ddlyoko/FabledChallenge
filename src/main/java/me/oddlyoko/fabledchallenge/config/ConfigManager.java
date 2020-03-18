package me.oddlyoko.fabledchallenge.config;

import java.io.File;

import lombok.Getter;
import me.oddlyoko.fabledchallenge.__;

@Getter
public class ConfigManager {
	private Config config;
	private String chatMessage;
	private String itemLorePlayer;

	public ConfigManager() {
		config = new Config(new File("plugins" + File.separator + __.NAME + File.separator + "config.yml"));
		chatMessage = config.getString("chatMessage");
		itemLorePlayer = config.getString("itemLorePlayer");
	}
}
