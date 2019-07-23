package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is class representing the tile in game. Each tile is initialized when game starts,
 * all its edge features are initialized and store in the tileFeatureList, following the
 * order up -> left -> down -> right. The initial orient for this tile is UP.
 * Before this tile is placed, the orientation of this tile should be set by calling setOrientType().
 * After this tile is placed on the game board, the orientation of its edge features should be
 * adjusted according to the tile's current orientation.
 */
public class Tile {
	private static final int DIR_TYPE = 4;
	private static final int HASHCODE_MAGIC_NUMBER = 31;
	private TileType tileType;
	private int tileId;
	private OrientType orient = OrientType.UP;
	private List<Feature> tileFeatureList;
	private List<List<OrientType>> featureOrientList;
	private List<Integer> dirList;
	private MonasteryFeature monaFeature = null;
	private boolean hasCoatOfArm = false;

	/**
	 * Constructor for tile, initialize all edge features in tile given tile type,
	 * including the features needs to be combined, the monastery features and coat of arm.
	 * @param tileType tile type
	 * @param tileId   id assigned to this tile
	 */
	public Tile(TileType tileType, int tileId) {
		this.tileType = tileType;
		this.tileId = tileId;
		initFeatures();
		initSpecialMarks();
		initCombinedFeatures();
		initNonCombinedFeatures();
	}


	/**
	 * set the orient before placing this tile.
	 * @param orient orient type
	 */
	void setOrientType(OrientType orient) {
		this.orient = orient;
	}

	/**
	 * initialize the orient information for edge features when placing this tile.
	 * @param pos position for placing this tile
	 */
	void placeTile(Position pos) {
		// use set to avoid duplication
		Set<Feature> features = new HashSet<>();

		for (int i = 0; i < DIR_TYPE; i++) {
			Feature feature =  tileFeatureList.get(i);
			List<OrientType> orients = featureOrientList.get(i);
			if (feature == null) continue;
			if (features.contains(feature)) continue;
			List<OrientType> newOrients = changeOrients(orients);
			// rotate orients
			feature.addNewOrients(pos, newOrients);
			features.add(feature);
		}
	}


	/**
	 * get the edge feature in tile based on its original orient(before placing).
	 * @param orient original orient
	 * @return edge feature in tile
	 */
	Feature getFeatureByOrient(OrientType orient) {
		int id = orient.getOrientType();
		return tileFeatureList.get(id);
	}

	/**
	 * change the original edge feature to another edge feature.
	 * @param orignal original feature
	 * @param now     features now
	 */
	void changeFeature(Feature orignal, Feature now) {
		for (int i = 0; i < tileFeatureList.size(); i++) {
			Feature f = tileFeatureList.get(i);
			if (f == orignal) {
				tileFeatureList.set(i, now);
			}
		}
	}

	/**
	 * get the edge feature in tile based on tile's current orient(after placing).
	 * @param orient tile's orient aftering placing
	 * @return edge feature in tile
	 */
	Feature getRotateFeatureByOrient(OrientType orient) {
		int offset = (getOrient().getOrientType() - OrientType.UP.getOrientType()) % DIR_TYPE;
		int newOrientVal = ((orient.getOrientType() + offset) % DIR_TYPE + DIR_TYPE) % DIR_TYPE;
		return tileFeatureList.get(newOrientVal);
	}

	/**
	 * get four edge features of this tile.
	 * @return list of edge features (ordered)
	 */
	List<Feature> getTileFeatures() {
		return tileFeatureList;
	}


	/**
	 * get orient of four edge features of this tile.
	 * @return list of edge feature orients (ordered)
	 */
	List<List<OrientType>> getFeatureOrients() {
		return featureOrientList;
	}

	/**
	 * return whether this tile has monastery.
	 * @return true/false
	 */
	boolean hasMonastery() {
		return monaFeature != null;
	}

	/**
	 * return monastery feature if any.
	 * @return monastery feature
	 */
	MonasteryFeature getMonastery() {
		return monaFeature;
	}

	/**
	 * Override toString method.
	 * @return string representation
	 */
	@Override
	public String toString() {
		return tileType.name();
	}


	// initialize all data structures for storing features and orients.
	private void initFeatures() {
		dirList = new ArrayList<>();
		tileFeatureList = new ArrayList<>();
		featureOrientList = new ArrayList<>();
		for (int i = 0; i < DIR_TYPE; i++) {
			dirList.add(i);
			tileFeatureList.add(null);
			featureOrientList.add(null);
		}
	}

	// initialize monastery feature and coat of arm.
	private void initSpecialMarks() {
		// get monastery and coat of arm information
		if (tileType.hasCoatOfArm()) { hasCoatOfArm = true; }
		if (tileType.hasMonastery()) {
			monaFeature = new MonasteryFeature(this);
		}
	}

	// initialize all the combined features.
	private void initCombinedFeatures() {
		// get 4 feature types information
		FeatureType[] featureTypes = tileType.getFeatureTypes();
		int[][] combinedIndexes = tileType.getCombinedIndex();

		if (combinedIndexes == null) { return; }

		// initialize combined features
		for (int[] combinedIndex: combinedIndexes) {
			int typeId = combinedIndex[0];

			// get this feature type
			FeatureType type = featureTypes[typeId];
			Feature newFeature = createNewFeature(type);

			// get orients for this combined feature
			List<OrientType> orients = new ArrayList<>();
			for (int index: combinedIndex) {
				OrientType orientType = tileType.getOrientType(index);
				orients.add(orientType);
			}

			for (int index: combinedIndex) {
				dirList.remove((Integer)index);
				tileFeatureList.set(index, newFeature);
				featureOrientList.set(index, orients);
			}
		}
	}

	// initialize all the non-combined features.
	private void initNonCombinedFeatures() {
		FeatureType[] featureTypes = tileType.getFeatureTypes();

		for (int dir: dirList) {
			List<OrientType> orients = new ArrayList<>();
			FeatureType type = featureTypes[dir];
			OrientType orientType = tileType.getOrientType(dir);
			orients.add(orientType);
			Feature newFeature = createNewFeature(type);
			tileFeatureList.set(dir, newFeature);
			featureOrientList.set(dir, orients);
		}
	}


	// change the orient of features based on tile orients.
	private List<OrientType> changeOrients(List<OrientType> orients) {
		// get rotate offset
		int offset = (OrientType.UP.getOrientType() - orient.getOrientType()) % DIR_TYPE;

		// get new orients of features by adding offset
		List<OrientType> newOrients = new ArrayList<>();
		for (OrientType orient: orients) {
			int newOrientVal = ((orient.getOrientType() + offset) % DIR_TYPE + DIR_TYPE) % DIR_TYPE;
			OrientType newOrient = OrientType.getOrientById(newOrientVal);
			newOrients.add(newOrient);
		}
		return newOrients;
	}

	// helper function for creating new features based on feature type.
	private Feature createNewFeature(FeatureType type) {
		if (type == FeatureType.CITY) {
			int coatOfArm = hasCoatOfArm ? 1: 0;
			return new CityFeature(this, coatOfArm);
		} else if (type == FeatureType.ROAD) {
			return new RoadFeature(this);
		} else if (type == FeatureType.MONASTERY) {
			return new MonasteryFeature(this);
		} else {
			return null;
		}
	}

	/*
	 * get the orient of this tile.
	 */
	private OrientType getOrient() {
		return this.orient;
	}

	/**
	 * get type of this tile.
	 * @return tile type
	 */
	public TileType getTileType() {
		return this.tileType;
	}

	/**
	 * Override equal function.
	 * @param other other tile
	 * @return equal or not
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Tile)) { return false; }
		Tile o = (Tile) other;
		return o.tileType.getTileId() == this.tileType.getTileId() && o.tileId == this.tileId;
	}

	/**
	 * return hashcode of this tile.
	 * @return hashcode
	 */
	@Override
	public int hashCode() {
		return this.tileType.getTileId() * HASHCODE_MAGIC_NUMBER + this.tileId;
	}



}