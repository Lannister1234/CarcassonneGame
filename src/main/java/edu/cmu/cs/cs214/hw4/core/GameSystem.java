package edu.cmu.cs.cs214.hw4.core;

import java.util.*;

/**
 * This is class implements the core logic of game. The game is initialized by number of players,
 * a shuffled tile stack and a empty game board. The start tile is put at the middle of the game board.
 * The system can pick a valid tile from tile stack (valid means there is at least one position on the
 * game board that player can place this tile). Player can put the tile with desired position and orientation
 * and meeple position. The system will return false if this placement is invalid or true if this placement
 * is successful. The system should be able to update the score after each movement.
 *
 * When the tile stack is empty, the game system should be able to end game automatically and calculate final
 * score for each player.
 */
public class GameSystem {
	public static final int MAX_PLAYERS = 5;
	public static final int MIN_PLAYERS = 2;
	private static final int ORIENT_TYPE_NUM = 4;

	private final List<GameChangeListener> gameChangeListeners = new ArrayList<>();

	private static TileType tileType = TileType.A;
	private Stack<Tile> tileStack;
	private List<Player> players;
	private int currPlayerIdx = 0;
	private int totalPlayerNum;
	private GameBoard gameBoard;
	private boolean isEnd = false;

	/**
	 * constructor for game system.
	 */
	public GameSystem() { }

	/**
	 * set the initial tile stack ,the players and game borad for this
	 * game, then put the start tile at the desired position.
	 * @param numPlayers player numbers
	 * @param placeStartTile for testing only, should start tile been placed automatically
	 * @throws IllegalArgumentException throws exception if number of players is incorrect
	 */
	public void startNewGame(int numPlayers, boolean placeStartTile) throws IllegalArgumentException {
		if (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) {
			throw new IllegalArgumentException("incorrect player numbers");
		}
		totalPlayerNum = numPlayers;
		// initialize players
		players = new ArrayList<>();
		tileStack = new Stack<>();
		for (int i = 0; i < numPlayers; i++) {
			String playerName = "player_" + i;
			Player newPlayer = new Player(playerName);
			players.add(newPlayer);
		}

		// initialize all tiles, push them to tile stack
		int tileTypeSize = tileType.getTileTypeNum();
		for (int i = 0; i < tileTypeSize; i++) {
			TileType newType = tileType.getTileType(i);
			int tileTypeNum = newType.getTileNum();
			for (int j = 0; j < tileTypeNum; j++) {
				Tile newTile = new Tile(newType, j);
				tileStack.push(newTile);
			}
		}

		// shuffle tile stack
		Collections.shuffle(tileStack);

		// initialize gameboard
		gameBoard = new GameBoard();

		// put start tile on gameboard
		// this parameter only for testing
		if (placeStartTile) {
			Tile startTile = tileStack.pop();
			Position startPos = new Position(GameBoard.BOARD_SIZE / 2, GameBoard.BOARD_SIZE / 2);
			boolean suceess = placeTile(startTile, startPos, OrientType.UP, null);
			assert suceess;
		}
		notifyScoreChanged();
		notifyPlayerChanged();
		notifyMeepleNumberChanged();
	}


	/**
	 * Get a random tile from tile stack, if this random tile does not match any positions in
	 * the game board, discard this tile and fetch a new one. Call end game if all the tiles in
	 * the tile stack has been used up.
	 * @return valid tile
	 */
	public Tile pickTileFromStack() {
		// check whether tile stack is empty
		if (tileStack.isEmpty()) {
			System.out.println("no tile left in tile stack, game is over!");
			endGame();
			return null;
		}

		// check does game board has at least one valid position for this new tile,
		// if not, discard this tile and pop a new tile instead
		Tile newTile = null;
		int flag = 0;
		while (true) {
			if (tileStack.isEmpty()) {break;}
			newTile = tileStack.pop();
			for (int i = 0; i < ORIENT_TYPE_NUM; i++) {
				OrientType orient = OrientType.getOrientById(i);
				newTile.setOrientType(orient);
				if(gameBoard.hasValidPositions(newTile)) {
					flag = -1;
					break;
				}
			}
			if (flag < 0) { break; }
		}
		notifyNewTileChanged(newTile);
		return newTile;
	}


	/**
	 * player place tile with desired position and orient, and meeple position. If the position
	 * is not valid or this tile does not match with adjcent tiles in features, or the specified
	 * meeple position is not valid for placing a meeple, or the player has used all its meeples
	 * but still wants to place a meeple, return false. Otherwise, the placement is successful.
	 * @param tile tile
	 * @param pos  position of the tile
	 * @param orient orientation of the tile
	 * @param meeplePos meeple position in this tile, null if there is no meeple.
	 * @return success or not
	 */
	public boolean placeTile(Tile tile, Position pos, OrientType orient, MeeplePosition meeplePos) {
		if (tile == null) return false;

		// get current player
		Player currPlayer = players.get(currPlayerIdx);

		if (meeplePos != null && !currPlayer.hasMeeple()) {
			System.out.println("there is no meeple left for this player.");
			return false;
		}

		// place tile in game board
		System.out.println("currPlayer: " + currPlayer);
		if(!gameBoard.placeNewTile(currPlayer, tile, pos, meeplePos, orient)) {
			return false;
		}

		Position newPos = new Position(GameBoard.BOARD_SIZE - pos.getY(), pos.getX());
		String meepleId = currPlayer.getName() + "_" + currPlayer.getLastMeepleId();
		notifyBoardChanged(tile, newPos, meepleId, meeplePos);

		// get all score features from gameboard
		List<Feature> features = gameBoard.getCompleteFeatures();
		Set<Feature> featureSet = new HashSet<>(features);

		// calculate feature scores and return meeples
		addScoreForWinner(featureSet);

		notifyMeepleNumberChanged();
		notifyScoreChanged();

		// change current player to next player
		currPlayerIdx = (currPlayerIdx + 1) % totalPlayerNum;

		notifyPlayerChanged();
		return true;
	}


	/**
	 * Game ends when no tile left, calculate and collect each player's score.
	 */
	void endGame() {
		System.out.println("Game over!");
		isEnd = true;
		Set<Feature> features = gameBoard.getInCompleteFeatures();
		addScoreForWinner(features);
		for (Player player: players) {
			System.out.println(player.getName() + " get score: " + player.getScore());
		}
		List<Player> winPlayers = new ArrayList<>();

		int maxVal = 0;
		for (Player player: players) {
			maxVal = Math.max(player.getScore(), maxVal);
		}

		// get winner with this max_val
		for (Player player: players) {
			if (player.getScore() == maxVal) {
				winPlayers.add(player);
			}
		}

		notifyEndGameChanged(winPlayers);
		notifyScoreChanged();
	}

	// helper function for adding scores for winners
	private void addScoreForWinner(Set<Feature> features) {
		if (features.isEmpty()) { return; }

		for (Feature feature: features) {
			int score = feature.calculateScore(isEnd);
			Set<Player> winPlayers = feature.getWinners();
			Set<Meeple> meeples = feature.getReturnMeeples();
			System.out.println(meeples);
			notifyReturnMeepleChanged(meeples);
			for (Player winPlayer: winPlayers) {
				winPlayer.addScore(score);
			}
		}
		notifyScoreChanged();
	}

	// get player name and player scores
	private Map<String, Integer> getPlayerScores() {
		Map<String, Integer> map = new HashMap<>();
		for (Player player: players) {
			map.put(player.getName(), player.getScore());
		}
		return map;
	}

	// get player name and left meeples
	private Map<String, Integer> getPlayerMeeples() {
		Map<String, Integer> map = new HashMap<>();
		for (Player player: players) {
			map.put(player.getName(), player.getLeftMeeples());
		}
		return map;
	}

	private void notifyNewTileChanged(Tile newTile) {
		for (GameChangeListener listener: gameChangeListeners) {
			listener.newTileChanged(newTile);
		}
	}

	private void notifyBoardChanged(Tile tile, Position pos, String meepleId, MeeplePosition mPos) {
		String playerName = players.get(currPlayerIdx).getName();
		for (GameChangeListener listener: gameChangeListeners) {
			listener.boardChanged(tile, pos, playerName, meepleId, mPos);
		}
	}

	private void notifyScoreChanged() {
		Map<String, Integer> scoreMap = getPlayerScores();
		System.out.println("score map: " + scoreMap.toString());
		for (GameChangeListener listener: gameChangeListeners) {
			listener.scoreChanged(scoreMap);
		}
	}

	private void notifyPlayerChanged() {
		String playerName = players.get(currPlayerIdx).getName();
		for (GameChangeListener listener: gameChangeListeners) {
			listener.playerChanged(playerName);
		}
	}

	private void notifyMeepleNumberChanged() {
		Map<String, Integer> meepleNumbers = getPlayerMeeples();
		for (GameChangeListener listener: gameChangeListeners) {
			listener.meepleNumberChanged(meepleNumbers);
		}
	}

	private void notifyEndGameChanged(List<Player> players) {
		List<String> playerNames = new ArrayList<>();
		for (Player player: players) {
			playerNames.add(player.getName());
		}
		for (GameChangeListener listener: gameChangeListeners) {
			listener.endGameChanged(playerNames);
		}
	}

	private void notifyReturnMeepleChanged(Set<Meeple> meeples) {
		for (GameChangeListener listener: gameChangeListeners) {
			listener.returnMeepleChanged(meeples);
		}
	}

	/**
	 * add game change listener.
	 * @param listener game change listener
	 */
	public void addGameChangeListener(GameChangeListener listener) {
		gameChangeListeners.add(listener);
	}

	/**
	 * get all players in game.
	 * @return list of players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * get how many tiles are left in tile stack.
	 * @return tile stack size
	 */
	public int getTilesLeft() {
		return tileStack.size();
	}
}
