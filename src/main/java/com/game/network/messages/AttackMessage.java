package com.game.network.messages;

import java.io.Serializable;

public class AttackMessage implements Serializable {
	
	private String attacker;
	private String opponent;

	public AttackMessage(String attacker, String opponent) {
		this.attacker = attacker;
		this.opponent = opponent;
		System.out.println("Attack Message Was Created");
	}

	public String getAttacker() {
		return attacker;
	}
	
	public String getOpponent() {
		return opponent;
	}
}