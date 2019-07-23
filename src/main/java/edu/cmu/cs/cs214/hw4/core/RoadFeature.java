package edu.cmu.cs.cs214.hw4.core;

import java.util.Set;
/**
 * This is the class representing road features in game.
 *
 */
public class RoadFeature extends Feature {
	private static final int UNIT_SCORE = 1;

	/**
	 * Constructor function for road feature.
	 * @param tile tile
	 */
	RoadFeature(Tile tile) {
		super(tile);
	}

	/**
	 * Calculate score for road feature.
	 * @param isEnd during the game or when game is finished
	 * @return score for road feature
	 */
	@Override
	int calculateScore(boolean isEnd) {
		Set<Tile> tiles = getTiles();
		return tiles.size()  * UNIT_SCORE;
	}

	/**
	 * check whether this road feature is complete.
	 * @return true/ false
	 */
	@Override
	boolean checkIsComplete() {
		Set<Orient> orients = getOrients();
		if (orients.isEmpty()) {
			setIsComplete(true);
		}
		return orients.isEmpty();
	}

	/**
	 * get feature type for road feature.
	 * @return road feature type
	 */
	@Override
	FeatureType getFeatureType() {
		return FeatureType.ROAD;
	}

	/**
	 * Override toString method.
	 * @return string representation
	 */
	@Override
	public String toString() {
		return "This is a road feature.";
	}
}
