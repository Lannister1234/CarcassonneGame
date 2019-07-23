package edu.cmu.cs.cs214.hw4.core;

import java.util.List;
import java.util.Set;

/**
 * This is the class that represents city features.
 */
public class CityFeature extends Feature {
	private static final int NORMAL_SCORE = 2;
	private static final int FINAL_SCORE = 1;
	private int numGoat;

	/**
	 * initialize city feature, set goat of arm number if any.
	 * @param tile tile
	 * @param numGoat of goat of arm
	 */
	CityFeature(Tile tile, int numGoat) {
		super(tile);
		this.numGoat = numGoat;
	}

	/**
	 * calculate score for city feature. 2 score for each tile and arm during the game,
	 * 1 score for each of them when game is finished.
	 * @param hasEnd has game ends
	 * @return score
	 */
	@Override
	int calculateScore(boolean hasEnd) {
		boolean isComplete = getIsComplete();
		Set<Tile> tiles = getTiles();
		if (!hasEnd) {
			if (isComplete) {
				return (tiles.size() + numGoat) * NORMAL_SCORE;
			} else {
				return 0;
			}
		} else {
			return (tiles.size() + numGoat) * FINAL_SCORE;
		}
	}

	/**
	 * combine this city with another city feature.
	 * @param other another feature
	 */
	@Override
	void combineFeatures(Feature other) {
		super.combineFeatures(other);
		this.numGoat += ((CityFeature) other).numGoat;
	}

	/**
	 * check whether this city feature is complete.
	 * @return true/false
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
	 * get feature type for city feature.
	 * @return city feature type
	 */
	@Override
	FeatureType getFeatureType() {
		return FeatureType.CITY;
	}


}
