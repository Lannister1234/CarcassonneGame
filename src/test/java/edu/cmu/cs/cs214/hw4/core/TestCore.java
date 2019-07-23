package edu.cmu.cs.cs214.hw4.core;

import org.junit.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.Set;


public class TestCore {
	private static final int BOARD_SIZE = 25;
	@Test
	public void testPlayer() {
		Player test_player = new Player("David");
		// test getName
		assertEquals("David", test_player.getName());

		// test getScore, addScore
		int add_val = 100;
		assertEquals(0, test_player.getScore());
		test_player.addScore(add_val);
		assertEquals(100, test_player.getScore());

		// test placeMeeple
		Position pos = new Position(0, 0);
		assertEquals(true, test_player.hasMeeple());
		for (int i = 0; i < 4; i++) {
			test_player.placeMeeple();
		}
		assertEquals(true, test_player.hasMeeple());

		for (int i = 0; i < 3; i++) {
			test_player.placeMeeple();
		}
		assertEquals(false, test_player.hasMeeple());
	}

	@Test
	public void testMeeple() {
		Player player = new Player("Tom");
		int id = 0;
		Meeple meeple = new Meeple(player, id);

		// test getPlayer
		assertEquals(player, meeple.getPlayer());

		// test place and return back
		assertEquals(false, meeple.isPlaced());
		meeple.place();
		assertEquals(true, meeple.isPlaced());
		meeple.getBack();
		assertEquals(false, meeple.isPlaced());

		// test set Meeple Position
		meeple.setMeeplePos(MeeplePosition.DOWN);
		assertEquals(MeeplePosition.DOWN, meeple.getMeeplePos());
		meeple.setMeeplePos(MeeplePosition.UP);
		assertEquals(MeeplePosition.UP, meeple.getMeeplePos());
	}


	@Test
	public void testTile1() {
		TileType tileType = TileType.A;
		Tile tile = new Tile(tileType, 0);
		// test getFeatureList
		List<Feature> featureList = tile.getTileFeatures();
		// get feature orients information
		List<List<OrientType>> featureOrients = tile.getFeatureOrients();
		assertEquals(null, featureList.get(0));
		assertEquals(null, featureList.get(1));
		assertEquals(FeatureType.ROAD, featureList.get(2).getFeatureType());
		assertEquals(null, featureList.get(3));

		assertEquals(OrientType.UP, featureOrients.get(0).get(0));
		assertEquals(OrientType.LEFT, featureOrients.get(1).get(0));
		assertEquals(OrientType.DOWN, featureOrients.get(2).get(0));
		assertEquals(OrientType.RIGHT, featureOrients.get(3).get(0));

		assertEquals(true, tile.hasMonastery());
	}


	@Test
	public void testTile2() {
		TileType tileType = TileType.F;
		Tile tile = new Tile(tileType, 0);
		// test getFeatureList
		List<Feature> featureList = tile.getTileFeatures();
		System.out.println(featureList);
		// get feature orients information
		List<List<OrientType>> featureOrients = tile.getFeatureOrients();
		System.out.println(featureOrients);
		assertEquals(null, featureList.get(0));
		assertEquals(FeatureType.CITY, featureList.get(1).getFeatureType());
		assertEquals(null, featureList.get(2));
		assertEquals(FeatureType.CITY, featureList.get(3).getFeatureType());

		assertEquals(OrientType.UP, featureOrients.get(0).get(0));
		assertEquals(Arrays.asList(OrientType.LEFT, OrientType.RIGHT), featureOrients.get(1));
		assertEquals(OrientType.DOWN, featureOrients.get(2).get(0));
		assertEquals(Arrays.asList(OrientType.LEFT, OrientType.RIGHT), featureOrients.get(3));

		assertEquals(false, tile.hasMonastery());
	}


	@Test
	public void testTile3() {
		TileType tileType = TileType.O;
		Position pos = new Position(0, 0);
		Tile tile = new Tile(tileType, 0);
		// test getFeatureList
		List<Feature> featureList = tile.getTileFeatures();
		// get feature orients information
		List<List<OrientType>> featureOrients = tile.getFeatureOrients();
		assertEquals(FeatureType.CITY, featureList.get(0).getFeatureType());
		assertEquals(FeatureType.CITY, featureList.get(1).getFeatureType());
		assertEquals(FeatureType.ROAD, featureList.get(2).getFeatureType());
		assertEquals(FeatureType.ROAD, featureList.get(3).getFeatureType());

		assertEquals(Arrays.asList(OrientType.UP, OrientType.LEFT), featureOrients.get(0));
		assertEquals(Arrays.asList(OrientType.UP, OrientType.LEFT), featureOrients.get(1));
		assertEquals(Arrays.asList(OrientType.DOWN, OrientType.RIGHT), featureOrients.get(2));
		assertEquals(Arrays.asList(OrientType.DOWN, OrientType.RIGHT), featureOrients.get(3));

		// change orientation
		tile.setOrientType(OrientType.DOWN);
		tile.placeTile(pos);

		// expected orient set
		Set<Orient> orientSet1 = new HashSet<>();
		Set<Orient> orientSet2 = new HashSet<>();
		Orient orient1 = new Orient(pos, OrientType.DOWN);
		Orient orient2 = new Orient(pos, OrientType.RIGHT);
		Orient orient3 = new Orient(pos, OrientType.UP);
		Orient orient4 = new Orient(pos, OrientType.LEFT);
		orientSet1.add(orient1);
		orientSet1.add(orient2);
		orientSet2.add(orient3);
		orientSet2.add(orient4);

		// assert set equals
		assertEquals(orientSet1, featureList.get(0).getOrients());
		assertEquals(orientSet1, featureList.get(1).getOrients());
		assertEquals(orientSet2, featureList.get(2).getOrients());
		assertEquals(orientSet2, featureList.get(3).getOrients());

		assertEquals(false, tile.hasMonastery());
	}


	@Test
	public void testTile4() {
		TileType tileType = TileType.H;
		Tile tile = new Tile(tileType, 0);
		Position pos = new Position(1, 1);
		// test getFeatureList
		List<Feature> featureList = tile.getTileFeatures();
		// get feature orients information
		List<List<OrientType>> featureOrients = tile.getFeatureOrients();
		assertEquals(null, featureList.get(0));
		assertEquals(FeatureType.CITY, featureList.get(1).getFeatureType());
		assertEquals(null, featureList.get(2));
		assertEquals(FeatureType.CITY, featureList.get(3).getFeatureType());

		assertEquals(Arrays.asList(OrientType.UP), featureOrients.get(0));
		assertEquals(Arrays.asList(OrientType.LEFT), featureOrients.get(1));
		assertEquals(Arrays.asList(OrientType.DOWN), featureOrients.get(2));
		assertEquals(Arrays.asList(OrientType.RIGHT), featureOrients.get(3));

		// change orientation
		tile.setOrientType(OrientType.RIGHT);
		tile.placeTile(pos);

		// expected orient set
		Set<Orient> orientSet2 = new HashSet<>();
		Set<Orient> orientSet4 = new HashSet<>();

		Orient orient2 = new Orient(pos, OrientType.DOWN);
		Orient orient4 = new Orient(pos, OrientType.UP);
		orientSet2.add(orient2);
		orientSet4.add(orient4);

		// assert set equals
		assertEquals(orientSet2, featureList.get(1).getOrients());
		assertEquals(orientSet4, featureList.get(3).getOrients());

		assertEquals(false, tile.hasMonastery());
	}

	@Test // Type i (up) (0, 0) + Type j (right) (1, 0)
	public void testCityFeature1() {
		Tile tile1 = new Tile(TileType.I, 0);
		Tile tile2 = new Tile(TileType.J, 0);
		Player player = new Player("Amy");
		Position pos1 = new Position(0, 0);
		Position pos2 = new Position(1, 0);
		Set<Player> players = new HashSet<>();
		players.add(player);
		Meeple meeple = new Meeple(player, 1);

		tile1.setOrientType(OrientType.UP);
		tile1.placeTile(pos1);
		tile2.setOrientType(OrientType.RIGHT);
		tile2.placeTile(pos2);

		Feature cityFeature1 = tile1.getFeatureByOrient(OrientType.RIGHT);
		cityFeature1.addMeeple(player);
		Feature cityFeature2 = tile2.getFeatureByOrient(OrientType.UP);
		cityFeature1.combineFeatures(cityFeature2);
		assertEquals(true, cityFeature1.checkIsComplete());

		// assert winners
		assertEquals(players, cityFeature1.getWinners());

		// assert scores
		assertEquals(4, cityFeature1.calculateScore(false));

		Feature cityFeature3 = tile1.getFeatureByOrient(OrientType.DOWN);
		assertEquals(0, cityFeature3.calculateScore(false));
		assertEquals(1, cityFeature3.calculateScore(true));
	}

	@Test // Type O (up) (1, 1) + Type K (up) (0, 1) + Type H (RIGHT) (1, 0)
	public void testCityFeature2() {
		Tile tile1 = new Tile(TileType.O, 0);
		Tile tile2 = new Tile(TileType.K, 0);
		Tile tile3 = new Tile(TileType.H, 0);
		Position pos1 = new Position(1, 0);
		Position pos2 = new Position(0, 0);
		Position pos3 = new Position(1, 1);

		Set<Player> players = new HashSet<>();

		tile1.setOrientType(OrientType.UP);
		tile1.placeTile(pos1);
		tile2.setOrientType(OrientType.UP);
		tile2.placeTile(pos2);
		tile3.setOrientType(OrientType.RIGHT);
		tile3.placeTile(pos3);

		Feature cityFeature1 = tile1.getFeatureByOrient(OrientType.UP);
		Feature cityFeature2 = tile2.getFeatureByOrient(OrientType.RIGHT);
		Feature cityFeature3 = tile3.getFeatureByOrient(OrientType.LEFT);
		cityFeature1.combineFeatures(cityFeature2);
		assertEquals(false, cityFeature1.checkIsComplete());
		cityFeature1.combineFeatures(cityFeature3);
		assertEquals(true, cityFeature1.checkIsComplete());

		// assert winners
		assertEquals(players, cityFeature1.getWinners());

		// assert scores
		assertEquals(8, cityFeature1.calculateScore(false));

		Feature cityFeature4 = tile3.getFeatureByOrient(OrientType.RIGHT);
		assertEquals(0, cityFeature4.calculateScore(false));
		assertEquals(1, cityFeature4.calculateScore(true));
	}


	@Test // Type T (up) (0, 2) + Type U (up) (0, 1) + Type W (left) (0, 0)
	public void testRoadFeature1() {
		Tile tile1 = new Tile(TileType.T, 0);
		Tile tile2 = new Tile(TileType.U, 0);
		Tile tile3 = new Tile(TileType.W, 0);
		// Amy place a meeple in tile 1, winner
		Player player = new Player("Amy");
		Position pos1 = new Position(0, 2);
		Position pos2 = new Position(0, 1);
		Position pos3 = new Position(0, 0);
		Set<Player> players = new HashSet<>();
		players.add(player);

		tile1.setOrientType(OrientType.UP);
		tile1.placeTile(pos1);
		tile2.setOrientType(OrientType.UP);
		tile2.placeTile(pos2);
		tile3.setOrientType(OrientType.LEFT);
		tile3.placeTile(pos3);

		Feature roadFeature1 = tile1.getFeatureByOrient(OrientType.DOWN);
		roadFeature1.addMeeple(player);
		Feature roadFeature2 = tile2.getFeatureByOrient(OrientType.UP);
		Feature roadFeature3 = tile3.getFeatureByOrient(OrientType.LEFT);

		roadFeature1.combineFeatures(roadFeature2);
		assertEquals(false, roadFeature1.checkIsComplete());
		roadFeature1.combineFeatures(roadFeature3);
		assertEquals(true, roadFeature1.checkIsComplete());

		// assert winners
		assertEquals(players, roadFeature1.getWinners());

		// assert scores
		assertEquals(3, roadFeature1.calculateScore(false));

		Feature roadFeature4 = tile3.getFeatureByOrient(OrientType.DOWN);
		assertEquals(1, roadFeature4.calculateScore(true));
	}


	@Test // A (up) (0, 2) + D (down) (0, 1) + J (right) (0, 0) + X (up) (1, 0)
	public void testRoadFeature2() {
		Tile tile1 = new Tile(TileType.J, 0);
		Tile tile2 = new Tile(TileType.D, 0);
		Tile tile3 = new Tile(TileType.A, 0);
		Tile tile4 = new Tile(TileType.X, 0);
		// Amy place meeples in tile 1 and 4 (winner), cathy place a meeple in tile 2
		Player player1 = new Player("Amy");
		Player player2 = new Player("Cathy");

		Position pos1 = new Position(0, 0);
		Position pos2 = new Position(0, 1);
		Position pos3 = new Position(0, 2);
		Position pos4 = new Position(1, 0);

		Set<Player> players = new HashSet<>();
		players.add(player1);

		tile1.setOrientType(OrientType.RIGHT);
		tile1.placeTile(pos1);
		tile2.setOrientType(OrientType.DOWN);
		tile2.placeTile(pos2);
		tile3.setOrientType(OrientType.UP);
		tile3.placeTile(pos3);
		tile4.setOrientType(OrientType.UP);
		tile4.placeTile(pos4);

		Feature roadFeature1 = tile1.getFeatureByOrient(OrientType.RIGHT);
		roadFeature1.addMeeple(player1);
		Feature roadFeature2 = tile2.getFeatureByOrient(OrientType.UP);
		roadFeature2.addMeeple(player2);
		Feature roadFeature3 = tile3.getFeatureByOrient(OrientType.DOWN);
		Feature roadFeature4 = tile4.getFeatureByOrient(OrientType.LEFT);
		roadFeature4.addMeeple(player1);

		roadFeature1.combineFeatures(roadFeature2);
		assertEquals(false, roadFeature1.checkIsComplete());
		roadFeature1.combineFeatures(roadFeature3);
		assertEquals(false, roadFeature1.checkIsComplete());
		roadFeature1.combineFeatures(roadFeature4);
		assertEquals(true, roadFeature1.checkIsComplete());

		// assert winners
		assertEquals(players, roadFeature1.getWinners());
		// assert scores
		assertEquals(4, roadFeature1.calculateScore(false));
	}


	@Test
	public void testMonasteryFeature() {
		Player player = new Player("Amy");
//		Meeple meeple = new Meeple(player, 0);
		Tile tile = new Tile(TileType.B, 0);
		Position pos1 = new Position(1,1);
		Set<Player> players = new HashSet<>();
		players.add(player);

		// place this tile at pos1
		tile.setOrientType(OrientType.UP);
		tile.placeTile(pos1);
		assertEquals(true, tile.hasMonastery());
		MonasteryFeature monastery = tile.getMonastery();

		// check meeple function
		assertEquals(false, monastery.hasMeeple());
		monastery.addMeeple(player);
		assertEquals(true, monastery.hasMeeple());

		// add neighbours, check isComplete and calculate score
		for (int i = 0; i < 4; i++) {
			monastery.addTileInMonastery();
		}
		assertEquals(5, monastery.calculateScore(false));
		assertEquals(false, monastery.checkIsComplete());
		for (int i = 0; i < 4; i++) {
			monastery.addTileInMonastery();
		}
		assertEquals(true, monastery.checkIsComplete());
		assertEquals(9, monastery.calculateScore(false));

		assertEquals(players, monastery.getWinners());
	}

	@Test
	public void testGameBoard() {
		GameBoard board = new GameBoard();
		Tile tile1 = new Tile(TileType.A, 0);
		Player player1 = new Player("Amy");

		Position pos1 = new Position(-1, 0);
		Position pos2 = new Position(0, 100);
		// index out of bound
		assertEquals(false, board.placeNewTile(player1, tile1, pos1, MeeplePosition.DOWN, OrientType.UP));
		assertEquals(false, board.placeNewTile(player1, tile1, pos2, null, OrientType.UP));

		// doesn't match neighbour tiles
		// H (up) (0, 0) + I (right) (1, 0)
		pos1 = new Position(0, 0);
		pos2 = new Position(1, 0);
		tile1 = new Tile(TileType.H, 0);
		Tile tile2 = new Tile(TileType.I, 0);

		assertEquals(true, board.placeNewTile(player1, tile1, pos1, null, OrientType.UP));
		assertEquals(false, board.placeNewTile(player1, tile2, pos2, null, OrientType.RIGHT));


		// doesn't match neighbour tiles
		// U (right) (0, 0) + V (up) (1, 0) + W (up) (1, 0)
		board = new GameBoard();
		tile1 = new Tile(TileType.U, 0);
		tile2 = new Tile(TileType.V, 0);
		Tile tile3 = new Tile(TileType.W, 0);
		pos1 = new Position(0, 0);
		pos2 = new Position(1, 0);
		Position pos3 = new Position(2, 0);
		assertEquals(true, board.placeNewTile(player1, tile1, pos1, null, OrientType.RIGHT));
		assertEquals(true, board.placeNewTile(player1, tile2, pos2, null, OrientType.UP));
		assertEquals(false, board.placeNewTile(player1, tile3, pos3, null, OrientType.UP));

	}

	@Test
	public void testGameBoard2() {
		Player player1 = new Player("Amy");
		// doesn't match neighbour tiles
		// J (left) + Q (down) + L (down)
		// I (up)   + O (up)
		GameBoard board = new GameBoard();
		Tile tile1 = new Tile(TileType.I, 0);
		Tile tile2 = new Tile(TileType.O, 0);
		Tile tile3 = new Tile(TileType.Q, 1);
		Tile tile4 = new Tile(TileType.L, 0);
		Tile tile5 = new Tile(TileType.J, 0);
		Position pos1 = new Position(2, 2);
		Position pos2 = new Position(3, 2);
		Position pos3 = new Position(3, 3);
		Position pos4 = new Position(4, 3);
		Position pos5 = new Position(2, 3);


		// add one meeple
		assertEquals(true, board.placeNewTile(player1, tile1, pos1, MeeplePosition.RIGHT, OrientType.UP));
		assertEquals(true, board.placeNewTile(player1, tile2, pos2, null, OrientType.UP));
		assertEquals(true, board.placeNewTile(player1, tile3, pos3, null, OrientType.DOWN));
		// conflict meeple
		assertEquals(false, board.placeNewTile(player1, tile4, pos4, MeeplePosition.LEFT, OrientType.DOWN));
		assertEquals(true, board.placeNewTile(player1, tile4, pos4, null, OrientType.DOWN));

		assertEquals(false, board.placeNewTile(player1, tile5, pos5, null, OrientType.LEFT));
	}



	@Test
	public void testGameBoard3() {
		Player player1 = new Player("Amy");

		GameBoard board = new GameBoard();
		Tile tile1 = new Tile(TileType.I, 0);
		Tile tile2 = new Tile(TileType.O, 0);
		Tile tile3 = new Tile(TileType.Q, 1);
		Tile tile4 = new Tile(TileType.L, 0);
		Tile tile5 = new Tile(TileType.H, 0);
		Position pos1 = new Position(2, 2);
		Position pos2 = new Position(3, 2);
		Position pos3 = new Position(3, 3);
		Position pos4 = new Position(4, 3);
		Position pos5 = new Position(2, 3);

		// add one meeple
		assertEquals(true, board.placeNewTile(player1, tile1, pos1, MeeplePosition.RIGHT, OrientType.UP));
		assertEquals(true, board.placeNewTile(player1, tile2, pos2, null, OrientType.UP));
		assertEquals(true, board.placeNewTile(player1, tile3, pos3, null, OrientType.DOWN));

		// conflict meeple
		assertEquals(true, board.placeNewTile(player1, tile4, pos4, MeeplePosition.UP, OrientType.DOWN));
		assertEquals(true, board.placeNewTile(player1, tile5, pos5, MeeplePosition.LEFT, OrientType.UP));

		// get scored features
		assertEquals(FeatureType.CITY, board.getCompleteFeatures().get(0).getFeatureType());
		assertEquals(2, board.getInCompleteFeatures().size());
	}


	@Test // test monastery case
	public void testGameBoard4() {
		Player player1 = new Player("Amy");
		GameBoard board = new GameBoard();

		Tile tile1 = new Tile(TileType.A, 0);
		Tile tile2 = new Tile(TileType.V, 0);
		Tile tile3 = new Tile(TileType.L, 0);
		Tile tile4 = new Tile(TileType.G,0);
		Tile tile5 = new Tile(TileType.V, 1);
		Tile tile6 = new Tile(TileType.L, 1);
		Tile tile7 = new Tile(TileType.W, 0);

		Position pos1 = new Position(2, 2);
		Position pos2 = new Position(2, 1);
		Position pos3 = new Position(3, 1);
		Position pos4 = new Position(1, 2);
		Position pos5 = new Position(2, 3);
		Position pos6 = new Position(2, 4);
		Position pos7 = new Position(3, 3);

		// try to place a non-monastery tile with meeple position mid.
		assertFalse(board.placeNewTile(player1, tile2, pos1, MeeplePosition.MID, OrientType.DOWN));

		// add one meeple
		assertEquals(true, board.placeNewTile(player1, tile1, pos1, MeeplePosition.MID, OrientType.UP));
		assertEquals(true, board.placeNewTile(player1, tile2, pos2, null, OrientType.DOWN));
		assertEquals(true, board.placeNewTile(player1, tile3, pos3, null, OrientType.UP));
		assertEquals(FeatureType.ROAD, board.getCompleteFeatures().get(0).getFeatureType());
		assertEquals(true, board.placeNewTile(player1, tile4, pos4, null, OrientType.DOWN));
		assertEquals(true, board.placeNewTile(player1, tile5, pos5, null, OrientType.DOWN));
		assertEquals(true, board.placeNewTile(player1, tile6, pos6, null, OrientType.UP));
		assertEquals(true, board.placeNewTile(player1, tile7, pos7, MeeplePosition.DOWN, OrientType.UP));
		assertEquals(FeatureType.ROAD, board.getCompleteFeatures().get(0).getFeatureType());

		// get scored features
		assertEquals(2, board.getInCompleteFeatures().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGameSystem1() throws IllegalArgumentException {
		GameSystem game = new GameSystem();
		game.startNewGame(6, true);
		GameSystem game1 = new GameSystem();
		game.startNewGame(1, true);
	}

	@Test
	public void testGameSystem2() {
		int total_tile_num = 72;
		GameSystem game = new GameSystem();
		game.startNewGame(3, true);
		for (int i = 0; i < total_tile_num - 1; i++) {
			game.pickTileFromStack();
		}
		assertNull(game.pickTileFromStack());
	}

	@Test
	public void testGameSystem3() {
		GameSystem game = new GameSystem();
		game.startNewGame(3, false);
		Tile tile1 = new Tile(TileType.A, 0);
		Tile tile2 = new Tile(TileType.V, 0);
		Tile tile3 = new Tile(TileType.L, 0);
		Tile tile4 = new Tile(TileType.G, 0);
		Tile tile5 = new Tile(TileType.V, 1);
		Tile tile6 = new Tile(TileType.L, 1);
		Tile tile7 = new Tile(TileType.W, 0);

		Position pos1 = new Position(2, 2);
		Position pos2 = new Position(2, 1);
		Position pos3 = new Position(3, 1);
		Position pos4 = new Position(1, 2);
		Position pos5 = new Position(2, 3);
		Position pos6 = new Position(2, 4);
		Position pos7 = new Position(3, 3);

		game.placeTile(tile1, pos1, OrientType.UP, MeeplePosition.MID); //1
		game.placeTile(tile2, pos2, OrientType.DOWN, MeeplePosition.UP);  // 2
		game.placeTile(tile3, pos3, OrientType.UP, null); // 3
		game.placeTile(tile4, pos4, OrientType.DOWN, MeeplePosition.UP); // 1
		game.placeTile(tile5, pos5, OrientType.DOWN, MeeplePosition.UP); // 2
		game.placeTile(tile6, pos6, OrientType.UP, MeeplePosition.LEFT); // 3
		game.placeTile(tile7, pos7, OrientType.UP, MeeplePosition.DOWN); // 1

		game.endGame();
	}
}
