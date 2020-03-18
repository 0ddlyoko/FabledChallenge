package me.oddlyoko.fabledchallenge.challenges;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Peer<E, F> {
	private E key;
	private F value;
}