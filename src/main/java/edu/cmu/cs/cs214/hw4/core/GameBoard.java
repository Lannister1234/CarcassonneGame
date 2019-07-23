package edu.cmu.cs.cs214.hw4.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This is class represents game board in game. When player place a tile on the game board,
 * the game board is responsible for checking positions, meeple position and neighbour features.
 * Only when all the conditions are matched, this placement is successful.
 * After placing a tile, the game board is responsible to check all its neighbour features
 * if there are any completed features for scoring.
 * When the game ends, the game board should be able to go through the entire game board to find
 * all the features which have not been scored and return them back to system for final scoring.
 */
public class GameBoard {
	public static final int BOARD_SIZE = 50;
	private Tile[][] board;
	private static final int[][] NEIGHBOUR_OFFSETS = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};
	private static final int[][] MONASTERY_OFFSETS = {{0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}};
	private static final OrientType[] NEIGHBOUR_ORIENTS = {OrientType.DOWN, OrientType.RIGHT, OrientType.UP, OrientType.LEFT};
	private static final OrientType[] MY_ORIENTS = {OrientType.UP, OrientType.LEFT, OrientType.DOWN, OrientType.RIGHT};

	private List<Feature> completeFeatures;
	private boolean isStartTile = true;

	/**
	 * initialize game board.
	 */
	GameBoard() {
		board = new Tile[BOARD_SIZE][BOARD_SIZE];
		isStartTile = true;
	}

	/**
	 * place a new tile by a player with specified position and meeple on game board.
	 * return false if invalid position or doesn't match neighbour features. Otherwise,
	 * add this tile in game board and update its edge features.
	 * @param player player
	 * @param tile tile place by player
	 * @param pos position of gameboard
	 * @param meeplePos meeple position on the tile, null if no meeple been placed
	 * @param orient orient type for this tile
	 * @return success or not
	 */
	boolean placeNewTile (Player player, Tile tile, Position pos, MeeplePosition meeplePos, OrientType orient) {
		assert player != null;
		assert tile != null;
		assert pos != null;

		// set orient for this tile
		tile.setOrientType(orient);

		// check position
		if (!checkBound(pos.getX(), pos.getY())) { return false; }

		// check with monastery case
		if (!checkMonasteryTile(tile, meeplePos)) { return false; }

		// check with neighbour tiles
		if (!checkNeighbourTiles(tile, pos, meeplePos)) { return false; }

		// check meeple position
		if (!checkMeeplePos(tile, meeplePos)) { return false; }

		// add to game board
		board[pos.getX()][pos.getY()] = tile;

		// place tile
		tile.placeTile(pos);

		// place meeple
		if (meeplePos != null ) {
			// placeMeeple(tile, meeplePos, player.placeMeeple(pos));
			placeMeeple(tile, meeplePos, player);
		}

		// combine features for tile if any
		combineFeaturesForTile(tile, pos);

		isStartTile = false;
		return true;
	}

	/**
	 * get any completed features after placing a new tile.
	 * @return list of completed features.
	 */
	List<Feature> getCompleteFeatures() {
		System.out.println("get complete features: " + completeFeatures);
		return completeFeatures;
	}

	/**
	 * get all the incomplete features in the game board for calculating
	 * final scores when the game is ended.
	 * @return set of Incompleted features
	 */
	Set<Feature> getInCompleteFeatures() {
		// use a set to avoid duplication
		Set<Feature> inCompleteFeatures = new HashSet<>();
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] == null) continue;
				Tile tile = board[i][j];
				if (tile.hasMonastery()) {
					MonasteryFeature m = tile.getMonastery();
					if (m.hasMeeple()) {
						inCompleteFeatures.add(m);
					}
				}
				List<Feature> tileFeatures = tile.getTileFeatures();
				for (Feature f: tileFeatures) {
					if (f == null) continue;
					if (inCompleteFeatures.contains(f)) continue;
					if (f.checkIsComplete()) continue;
					if (!f.hasMeeple()) continue;
					inCompleteFeatures.add(f);
				}
			}
		}
		return inCompleteFeatures;
	}

	// combine features in this tile with adjcent features
	private void combineFeaturesForTile(Tile tile, Position pos) {
		completeFeatures = new ArrayList<>();

		// loop through neighbour tiles
		for (int i = 0; i < NEIGHBOUR_ORIENTS.length; i++) {

			Tile neighbour = getNeighbourTile(i, pos);
			if (neighbour == null) continue;

			// get neighbour feature
			Feature neighbourFeature = neighbour.getRotateFeatureByOrient(NEIGHBOUR_ORIENTS[i]);
			Feature myFeature = tile.getRotateFeatureByOrient(MY_ORIENTS[i]);
			if (neighbourFeature == null || myFeature == null) continue;

			// combine with neighbour features
			myFeature.combineFeatures(neighbourFeature);

			if (!myFeature.checkIsComplete()) { continue; }

			// add features if complete
			completeFeatures.add(myFeature);
		}
		// check for complete monastery features
		completeFeatures.addAll(getCompleteMonastery(pos));
	}

	// add meeples in features
	private void placeMeeple(Tile tile, MeeplePosition meeplePos, Player player) {
		// add meeple in feature
		if (meeplePos != MeeplePosition.MID) {
			int mPos = meeplePos.getMeeplePos();
			OrientType orientType = OrientType.getOrientById(mPos);
			Feature feature = tile.getRotateFeatureByOrient(orientType);
			System.out.println("orientType: " + orientType);
			System.out.println("feature: " + feature);
			feature.addMeeple(player);
		} else {
			// if middle, must have monastery
			assert tile.hasMonastery();
			MonasteryFeature monaFeature = tile.getMonastery();
			monaFeature.addMeeple(player);
		}
	}


	private boolean checkMeeplePos(Tile tile, MeeplePosition mPos) {
		if (mPos == null || mPos == MeeplePosition.NOT_PLACED || mPos == MeeplePosition.MID) {
			return true;
		}
		int m = mPos.getMeeplePos();
		OrientType orientType = OrientType.getOrientById(m);
		if (tile.getRotateFeatureByOrient(orientType) == null) {
			System.out.println("meeple position is wrong");
			return false;
		}
		return true;
	}


	// check with neighbour features
	private boolean checkNeighbourTiles(Tile tile, Position pos, MeeplePosition meeplePos) {
		int meeplePosVal;
		Feature meepleFeature = null;
		if (meeplePos != null && meeplePos != MeeplePosition.MID && meeplePos != MeeplePosition.NOT_PLACED) {
			meeplePosVal = meeplePos.getMeeplePos();
			meepleFeature = tile.getRotateFeatureByOrient(OrientType.getOrientById(meeplePosVal));
		}

		boolean hasNeighbours = false;

		// loop through neighbour tiles
		for (int i = 0; i < NEIGHBOUR_ORIENTS.length; i++) {
			// get neighbour tile
			Tile neighbour = getNeighbourTile(i, pos);
			if (neighbour == null) continue;

			// get two orients for matching
			OrientType neighbourOrient = NEIGHBOUR_ORIENTS[i];
			OrientType myOrient = MY_ORIENTS[i];
			hasNeighbours = true;

			// get feature type of this neighbour
			Feature neighbourFeature = neighbour.getRotateFeatureByOrient(neighbourOrient);
			FeatureType featureType = null;
			if (neighbourFeature != null) { featureType = neighbourFeature.getFeatureType(); }

			Feature myFeature = tile.getRotateFeatureByOrient(myOrient);
			FeatureType myType = null;
			if (myFeature != null) { myType = myFeature.getFeatureType(); }

			if (featureType != myType) {
				System.out.println("this type does not match neighbour tile features.");
				System.out.println("my orient:" + myOrient.name() +  " feature:" + myType);
				System.out.println("nei orient:" + neighbourOrient.name() + " feature:" + featureType);
				return false;
			}

			// check if neighbour features has meeples
			if (meeplePos != null && meeplePos != MeeplePosition.NOT_PLACED &&
					neighbourFeature != null && meepleFeature == myFeature) {
				if (neighbourFeature.hasMeeple()) {
					System.out.println("There is already a meeple.");
					return false;
				}
			}
		}
		// if this position doesn't has neighbours, return false;
		if (isStartTile) { return true; }
		return hasNeighbours;
	}

	// get complete monastery in its surrounding tiles if any.
	private List<Feature> getCompleteMonastery(Position pos) {
		int x = pos.getX();
		int y = pos.getY();
		Tile thisTile = getTileByPosition(x, y);
		List<Feature> mFeatures = new ArrayList<>();
		if (thisTile == null) { return mFeatures;}

		if (thisTile.hasMonastery()) {
			MonasteryFeature mFeature = thisTile.getMonastery();
			for (int i = 0; i < MONASTERY_OFFSETS.length; i++) {
				int xOffset = MONASTERY_OFFSETS[i][0];
				int yOffset = MONASTERY_OFFSETS[i][1];
				Tile neighbour = getTileByPosition(x + xOffset, y + yOffset);
				if (neighbour == null) continue;
				mFeature.addTileInMonastery();
				if(mFeature.checkIsComplete()) {
					System.out.println("------there is a complete monastery.");
					mFeatures.add(mFeature);
				}
			}
		}
		for (int i = 0; i < MONASTERY_OFFSETS.length; i++) {
			int xOffset = MONASTERY_OFFSETS[i][0];
			int yOffset = MONASTERY_OFFSETS[i][1];
			Tile neighbour = getTileByPosition(x + xOffset, y + yOffset);
			if (neighbour == null) continue;
			if (!neighbour.hasMonastery()) { continue; }
			MonasteryFeature mFeature = neighbour.getMonastery();
			mFeature.addTileInMonastery();
			if(mFeature.checkIsComplete()) {
				System.out.println("------there is a complete monastery.");
				mFeatures.add(mFeature);
			}
		}


		return mFeatures;
	}

	// check special case: monastery features
	private boolean checkMonasteryTile(Tile tile, MeeplePosition meeplePos) {
		if (meeplePos != MeeplePosition.MID) { return true; }
		return tile.hasMonastery();
	}


	/**
	 * check if there is at least one valid position on the game board for this new tile.
	 * @param tile new tile
	 * @return true if there is at least one valid position, otherwise false
	 */
	boolean hasValidPositions(Tile tile) {
		for (int i = 0; i < BOARD_SIZE; i++) {
			for (int j = 0; j < BOARD_SIZE; j++) {
				if (board[i][j] != null) continue;
				// check if match neighbours tiles
				Position pos = new Position(i, j);
				if (checkNeighbourTiles(tile, pos, null)) {
					return true;
				}
			}
		}
		return false;
	}

	// get neighbour tiles
	private Tile getNeighbourTile(int index, Position pos) {
		int x = pos.getX();
		int y = pos.getY();
		int xOffset = NEIGHBOUR_OFFSETS[index][0];
		int yOffset = NEIGHBOUR_OFFSETS[index][1];
		return getTileByPosition(x + xOffset, y + yOffset);
	}


	// check whether this position is out of bound.
	private boolean checkBound(int x, int y) {
		if (x < 0 || x >= BOARD_SIZE) {
			return false;
		}
		if (y < 0 || y >= BOARD_SIZE) {
			return false;
		}
		return true;
	}

	// get tile on game board by position
	private Tile getTileByPosition(int x, int y) {
		if (checkBound(x, y)) {
			return board[x][y];
		} else {
			return null;
		}
	}
}
