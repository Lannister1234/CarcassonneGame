package edu.cmu.cs.cs214.hw4.core;

import java.util.List;
import java.util.ArrayList;

/**
 * This is the class representing players in game. A player has a list of meeples,
 * its score, and name. Player can pick an used meeple from his list to place.
 */
public class Player {
	private static final int INIT_MEEPLES = 7;
	private String name;
	private int score;
	private int numUsedMeeples = 0;
	private List<Meeple> meeples;
	private int lastMeepleId = -1;

	/**
	 * Constructor for player class. Initializing seven meeples for player.
	 */
	Player(String playerName) {
		name = playerName;
		score = 0;
		meeples = new ArrayList<>();
		for (int i = 0; i < INIT_MEEPLES; i++) {
			meeples.add(new Meeple(this, i));
		}
	}

	/**
	 * get user score.
	 * @return score
	 */
	int getScore() {
		return score;
	}

	/**
	 * add score to player.
	 * @param addedVal added score
	 */
	void addScore(int addedVal) {
		score += addedVal;
	}

	/**
	 * get username.
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * String representation for this player
	 * @return string
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * pick an unused meeple for placing.
	 */
	Meeple placeMeeple() {
		if (!hasMeeple()) { return null; }
		for (Meeple meeple: meeples) {
			if (!meeple.isPlaced()) {
				meeple.place();
				lastMeepleId = meeple.getId();
				numUsedMeeples++;
				return meeple;
			}
		}
		return null;
	}

	/**
	 * return a meeple.
	 */
	void returnMeeple() {
		numUsedMeeples--;
	}

	/**
	 * get left meeples number
	 * @return how many meeples are left
	 */
	int getLeftMeeples() {
		return INIT_MEEPLES - numUsedMeeples;
	}

	int getLastMeepleId() {
		return lastMeepleId;
	}

	/**
	 * check whether this players has unused meeples
	 * @return true/false
	 */
	boolean hasMeeple() {
		return numUsedMeeples != INIT_MEEPLES;
	}
}
