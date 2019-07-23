package edu.cmu.cs.cs214.hw4.core;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is the interface for game change listener.
 */
public interface GameChangeListener {
	/**
	 * get called when a new random new tile is assigned.
	 * @param tile new tile
	 */
	void newTileChanged(Tile tile);

	/**
	 * get called when current player is changed every round.
	 * @param playerName current player name
	 */
	void playerChanged(String playerName);

	/**
	 * get called when player scores are changed
	 * @param scores hashMap that contains (PlayerName, score) pair
	 */
	void scoreChanged(Map<String, Integer> scores);

	/**
	 * get called when player's meeple number is changed
	 * @param meeples hashMap that contains (PlayerName, left Meeple Number) pair
	 */
	void meepleNumberChanged(Map<String, Integer> meeples);

	/**
	 * get called when meeples on the game board are returned
	 * @param meeples set of meeples to be returned
	 */
	void returnMeepleChanged(Set<Meeple> meeples);

	/**
	 * get called when a new tile is placed on game board.
	 * @param tile       new tile
	 * @param pos        position
	 * @param playerName player name of who made this place
	 * @param meepleId   meeple string representation
	 * @param mPos       meeple position on new tile
	 */
	void boardChanged(Tile tile, Position pos, String playerName, String meepleId, MeeplePosition mPos);


	/**
	 * get called when game ends, calculate final scores and return winners.
	 * @param winners list that contains winners of game
	 */
	void endGameChanged(List<String> winners);
}
