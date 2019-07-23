package edu.cmu.cs.cs214.hw4.core;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * This is the class for tile type. A tileType class represents all the information in this typical type.
 * including name of this tile type, how many tiles in this tile type, how we order the edge features,
 * the information for combining edge features in a tile, and also some minor features like does this
 * tile type has monastery and coat of arm.
 */
public enum TileType {

	A(0), B(1), C(2), D(3), E(4), F(5), G(6), H(7), I(8), J(9), K(10), L(11), M(12),
	N(13), O(14), P(15), Q(16), R(17), S(18), T(19), U(20), V(21), W(22), X(23);

	private static final int TOTAL_TILE_TYPES = 24;

	// how to order the edge features (from up --> left --> down --> right)
	private static final OrientType[] IDX_2_ORIENT = {OrientType.UP, OrientType.LEFT, OrientType.DOWN, OrientType.RIGHT};

	// array for tile names
	private static final TileType[] TYPE_NAMES = {A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X};

	// array for how many tiles in this tile type
	private static final int[] TILE_NUMBERS = {2, 4, 1, 4, 5, 2, 1, 3, 2, 3, 3, 3, 2, 3, 2, 3, 1, 3, 2, 1, 8, 9, 4, 1};

	// the array contains information for combing edge features in a tile
	// if the entry for a tile type is {{0, 1}, {2, 3}}, it means feature in position 0 and position 1
	// has the same type and should be combined as one feature, so does position 2 and position 3.
	private static final int[][][] COMBINED_INDEX = {null, null, {{0, 1, 2, 3}}, {{0, 2}},
			null, {{1, 3}}, {{0, 2}}, null, null, {{2, 3}}, {{0, 1}}, null, {{0, 1}},
			{{0, 1}}, {{0, 1}, {2, 3}}, {{0, 1}, {2, 3}}, {{0, 1, 3}}, {{0, 1, 3}}, {{0, 1, 3}}, {{0, 1, 3}},
			{{0, 2}}, {{1, 2}}, null, null,
	};

	// the set contains index for tiles which has monastery
	private static final Set<Integer> MONASTERY_SET = new HashSet<>(Arrays.asList(0, 1));

	// the set contains index for tiles which has coat of arm
	private static final Set<Integer> COAT_OF_ARM_SET = new HashSet<>(Arrays.asList(2, 5, 12, 14, 16, 18));

	// the 2d array contains information of edge features for each tile, following the order up, left, down, right
	private static final FeatureType[][] TILE_FEATURE_TYPES = {
		{FeatureType.FIELD, FeatureType.FIELD, FeatureType.ROAD, FeatureType.FIELD},  // A
		{FeatureType.FIELD, FeatureType.FIELD, FeatureType.FIELD, FeatureType.FIELD}, // B
		{FeatureType.CITY, FeatureType.CITY, FeatureType.CITY, FeatureType.CITY},     // C
		{FeatureType.ROAD, FeatureType.FIELD, FeatureType.ROAD, FeatureType.CITY},    // D
		{FeatureType.CITY, FeatureType.FIELD, FeatureType.FIELD, FeatureType.FIELD},  // E
		{FeatureType.FIELD, FeatureType.CITY, FeatureType.FIELD, FeatureType.CITY},   // F
		{FeatureType.CITY, FeatureType.FIELD, FeatureType.CITY, FeatureType.FIELD},	  // G
		{FeatureType.FIELD, FeatureType.CITY, FeatureType.FIELD, FeatureType.CITY},   // H
		{FeatureType.FIELD, FeatureType.FIELD, FeatureType.CITY, FeatureType.CITY},   // I
		{FeatureType.CITY, FeatureType.FIELD, FeatureType.ROAD, FeatureType.ROAD},    // J
		{FeatureType.ROAD, FeatureType.ROAD, FeatureType.FIELD, FeatureType.CITY},    // K
		{FeatureType.ROAD, FeatureType.ROAD, FeatureType.ROAD, FeatureType.CITY},     // L
		{FeatureType.CITY, FeatureType.CITY, FeatureType.FIELD, FeatureType.FIELD},   // M
		{FeatureType.CITY, FeatureType.CITY, FeatureType.FIELD, FeatureType.FIELD},   // N
		{FeatureType.CITY, FeatureType.CITY, FeatureType.ROAD, FeatureType.ROAD},     // O
		{FeatureType.CITY, FeatureType.CITY, FeatureType.ROAD, FeatureType.ROAD},     // P
		{FeatureType.CITY, FeatureType.CITY, FeatureType.FIELD, FeatureType.CITY},    // Q
		{FeatureType.CITY, FeatureType.CITY, FeatureType.FIELD, FeatureType.CITY},    // R
		{FeatureType.CITY, FeatureType.CITY, FeatureType.ROAD, FeatureType.CITY},     // S
		{FeatureType.CITY, FeatureType.CITY, FeatureType.ROAD, FeatureType.CITY},     // T
		{FeatureType.ROAD, FeatureType.FIELD, FeatureType.ROAD, FeatureType.FIELD},   // U
		{FeatureType.FIELD, FeatureType.ROAD, FeatureType.ROAD, FeatureType.FIELD},   // V
		{FeatureType.FIELD, FeatureType.ROAD, FeatureType.ROAD, FeatureType.ROAD},    // W
		{FeatureType.ROAD, FeatureType.ROAD, FeatureType.ROAD, FeatureType.ROAD},     // X
	};

	// the tile id
	private final int tileId;

	/**
	 * Constructor for tileType.
	 * @param tileId id for tile type
	 */
	TileType(int tileId) throws IllegalArgumentException {
		if (tileId < 0 || tileId >= TOTAL_TILE_TYPES) {
			throw new IllegalArgumentException("tile type id out of bound");
		}
		this.tileId = tileId;
	}

	/**
	 * get FeatureType.
	 * @return feature type
	 */
	FeatureType[] getFeatureTypes() {
		return TILE_FEATURE_TYPES[tileId];
	}

	/**
	 * get index array for features needed to be combined. The first dimension
	 * represents list of combined features, the second dimension represents which
	 * edge features are in the combined features list.
	 * @return 2d array represents combine index
	 */
	int[][] getCombinedIndex() {
		return COMBINED_INDEX[tileId];
	}

	/**
	 * get OrientType with the corresponding index.
	 * @param index index
	 * @return the corresponding orientType
	 */
	OrientType getOrientType(int index) {
		return IDX_2_ORIENT[index];
	}

	/**
	 * get whether this tile type has monastery feature.
	 * @return true/ false
	 */
	boolean hasMonastery() {
		return MONASTERY_SET.contains(tileId);
	}

	/**
	 * get whether this tile type has coat of arm.
	 * @return true/false
	 */
	boolean hasCoatOfArm() {
		return COAT_OF_ARM_SET.contains(tileId);
	}

	/**
	 * get the total number of tile types.
	 * @return total number
	 */
	int getTileTypeNum() {
		return TYPE_NAMES.length;
	}

	/**
	 * get how many tiles does this tile type has.
	 * @return tile count
	 */
	int getTileNum() {
		return TILE_NUMBERS[tileId];
	}

	/**
	 * return associated tile type by id.
	 * @param id index
	 * @return tile type
	 */
	TileType getTileType(int id) {
		if (id < 0 || id >= getTileTypeNum()) {
			return null;
		} else {
			return TYPE_NAMES[id];
		}
	}

	/**
	 * get tile id
	 * @return tile id
	 */
	public int getTileId() {
		return tileId;
	}


}
