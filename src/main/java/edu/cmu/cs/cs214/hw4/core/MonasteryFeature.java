package edu.cmu.cs.cs214.hw4.core;
/**
 * This is the class representing monastery features in game.
 */
public class MonasteryFeature extends Feature {
	private static final int COMPLETE_NUMBER = 9;
	private static final int UNIT_SCORE = 1;
	private int numTiles = 1;

	/**
	 * Constructor function for monastery feature.
	 * @param tile tile
	 */
	MonasteryFeature(Tile tile) {
		super(tile);
	}

	/**
	 * Monastery feature does not need to combine with other features.
	 * @param other another feature
	 */
	@Override
	void combineFeatures(Feature other) { }

	/**
	 * add a number when placing a new tile in the surrounding positions
	 * of the monastery feature.
	 */
	void addTileInMonastery() {
		numTiles++;
		System.out.println("monastry num tiles: " + numTiles);
		checkIsComplete();
	}

	/**
	 * check if this monastery feature is complete. If the surrounding tile number
	 * is 8, this indicates that this monastery feature is complete.
	 * @return true / false
	 */
	boolean checkIsComplete() {
		boolean isComplete = getIsComplete();
		if (numTiles == COMPLETE_NUMBER) {
			isComplete = true;
			setIsComplete(true);
		}
		return isComplete;
	}

	/**
	 * calculate score for monastery feature, 1 score for each tile.
	 * @return score
	 */
	@Override
	int calculateScore(boolean isEnd) {
		return numTiles * UNIT_SCORE;
	}

	/**
	 * get feature type for monastery feature.
	 * @return feature type
	 */
	@Override
	FeatureType getFeatureType() {
		return FeatureType.MONASTERY;
	}

	/**
	 * Override toString method.
	 * @return string representation
	 */
	@Override
	public String toString() {
		return "This is a monastery feature.";
	}

}
