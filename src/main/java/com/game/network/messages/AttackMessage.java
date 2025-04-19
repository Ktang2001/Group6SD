package com.game.network.messages;

import java.io.Serializable;

public class AttackMessage implements Serializable {
	
	private String attacker;

	public AttackMessage(String attacker) {
		this.attacker = attacker;
		System.out.println("Attack Message Was Created")
	}

	public String getAttacker() {
		return attacker;
	}
}