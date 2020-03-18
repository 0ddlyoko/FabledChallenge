package me.oddlyoko.fabledchallenge.players;

import java.util.HashMap;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.oddlyoko.fabledchallenge.challenges.Challenge;

@Getter
@AllArgsConstructor
public class PlayerChallenge {
	private UUID uuid;
	private HashMap<Challenge, Integer> challenges;
}
