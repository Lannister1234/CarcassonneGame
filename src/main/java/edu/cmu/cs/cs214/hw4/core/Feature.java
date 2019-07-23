package edu.cmu.cs.cs214.hw4.core;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * This is class representing features in game. The feature type is initialized by a tile.
 * When a tile is placed, all the features in this tile are initialized with a list of orients.
 * when a meeple is placed in this feature, the feature should add it to the meeple list.
 * The feature should be able to combine with another feature and update its tile_list, meeple_list
 * and orient_list. The feature is marked as complete when the orient_list is empty(only applied to
 * road features and city features).
 */
public abstract class Feature {
	private boolean isComplete;
	private Set<Tile> tiles;
	private Set<Orient> orients;
	private Set<Meeple> meepleSet;

	/**
	 * initialize the edge feature given a tile.
	 * @param tile tile
	 */
	Feature(Tile tile) {
		tiles = new HashSet<>();
		tiles.add(tile);
		meepleSet = new HashSet<>();
		orients = new HashSet<>();
		isComplete = false;
	}

	/**
	 * combine this feature with another feature.
	 * @param other another feature
	 */
	void combineFeatures(Feature other) {
		if (other == null) { return; }

		// if feature type doesn't match, return
		if (this.getFeatureType() != other.getFeatureType()) { return; }

		// combine tiles
		this.tiles.addAll(other.tiles);

		// combine meeples
		this.meepleSet.addAll(other.meepleSet);

		// deep copy two hashSets
		HashSet<Orient> orientSet1 = new HashSet<>();
		HashSet<Orient> orientSet2 = new HashSet<>();
		orientSet1.addAll(this.orients);
		orientSet2.addAll(other.orients);

		for (Orient o: this.orients) {
			for (Orient o1: other.orients) {
				if (o.orientMatch(o1)) {
					orientSet1.remove(o);
					orientSet2.remove(o1);
				}
			}
		}
		this.orients  = orientSet1;
		other.orients = orientSet2;

		this.orients.addAll(other.orients);

		// set features in other tiles to this feature
		for (Tile otherTiles: other.tiles) {
			otherTiles.changeFeature(other, this);
		}

		// check if complete
		if (this.orients.isEmpty()) { isComplete = true; }
	}

	/**
	 * add new orients for this feature when placing the tile in game board.
	 * @param pos position for this feature
	 * @param orientList the new orients for edge feature
	 */
	void addNewOrients(Position pos, List<OrientType> orientList) {
		for (OrientType o: orientList) {
			Orient newOrient = new Orient(pos, o);
			orients.add(newOrient);
		}
	}

	/**
	 * A player add a meeple in this feature.
	 * @param player player
	 */
	void addMeeple(Player player) {
		Meeple m = player.placeMeeple();
		meepleSet.add(m);
	}

	/**
	 * whether this feature has a meeple.
	 * @return true/ false
	 */
	boolean hasMeeple() {
		return !meepleSet.isEmpty();
	}

	/**
	 * get who is the winner by counting all the meeples in feature,
	 * return back all the meeples in this feature.
	 * @return the set of winners
	 */
	Set<Player> getWinners() {
		Map<Player, Integer> meepleCount = new HashMap<>();
		Set<Player> winPlayers = new HashSet<>();
		int maxVal = 0;

		// no meeple, no winner
		if (meepleSet.isEmpty()) { return winPlayers; }

		// return back all meeples in completed features
		// get max_val for player's meeple number

		System.out.println("meeples:" + meepleSet);

		for (Meeple m: meepleSet) {
			m.getBack();
			Player player = m.getPlayer();
			meepleCount.put(player, meepleCount.getOrDefault(player, 0) + 1);
			maxVal = Math.max(meepleCount.get(player), maxVal);
		}

		// get winner with this max_val
		for (Map.Entry<Player, Integer> entry: meepleCount.entrySet()) {
			Player player = entry.getKey();
			int count = entry.getValue();
			if (count == maxVal) { winPlayers.add(player); }
		}
		return winPlayers;
	}

	/**
	 * get meeples in this feature
	 * @return list of meeples
	 */
	public Set<Meeple> getReturnMeeples() {
		return meepleSet;
	}

	/**
	 * Get all the orients in the feature.
	 * @return set of orient
	 */
	Set<Orient> getOrients() {
		return orients;
	}

	/**
	 * Get tiles field.
	 * @return list of tiles
	 */
	Set<Tile> getTiles() {
		return tiles;
	}

	/**
	 * get isComplete field.
	 * @return isComplete
	 */
	boolean getIsComplete() {
		return isComplete;
	}

	/**
	 * set isComplete field.
	 * @return isComplete
	 */
	void setIsComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	/**
	 * check whether this feature is complete.
	 * @return complete or not
	 */
	abstract boolean checkIsComplete();

	/**
	 * calculate the score for this feature, there are two cases,
	 * one is during the game, another is when game finishes.
	 * @param isEnd is the game ends
	 * @return score
	 */
	abstract int calculateScore(boolean isEnd);

	/**
	 * Get feature type for this feature.
	 * @return feature type
	 */
	abstract FeatureType getFeatureType();
}
