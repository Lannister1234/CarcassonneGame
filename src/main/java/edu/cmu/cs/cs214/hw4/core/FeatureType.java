package edu.cmu.cs.cs214.hw4.core;

/**
 * This is the enum class for feature type. Has four types: field, city
 * road, monastery. Field features are only used for initializing tiles,
 * other features are used in game system and game board.
 */
enum FeatureType {
	FIELD(0), CITY(1), ROAD(2), MONASTERY(3);
	private final int type;

	/**
	 * Constructor for this feature type.
	 * @param type type id
	 */
	FeatureType(int type) {
		this.type = type;
	}

//	public int getFeatureType() { return this.type; }
}
