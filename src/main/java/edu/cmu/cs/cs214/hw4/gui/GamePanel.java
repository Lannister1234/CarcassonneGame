package edu.cmu.cs.cs214.hw4.gui;

import edu.cmu.cs.cs214.hw4.core.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.Color;
import java.awt.Graphics2D;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.swing.*;

/**
 * This class implements game panel for Carcassonne game, this game panel is consisted of four parts,
 * 1. board panel, which have 2D Jbuttons for placing tiles and be able to resize.  2. player info panel,
 * which displays players name, scores, left meeples and meeple colors. 3. game info panel, which has
 * information like how many remaining tiles and whether the most recent placement is successful or not.
 * 4. Tile info panel, which displays the image of current tile, a rotate button which can rotate orientation
 * of this tile, a combo box which can choose the meeple position.
 */
public class GamePanel extends JPanel implements GameChangeListener {
	private static final int TOTAL_TILE_TYPES = 24;
	private static final int MEEPLE_SIZE = 10;
	private static final int TILE_IMAGE_SIZE = 90;
	private static final int MIN_PANEL_SIZE = 540;
	private static final int MARGIN_OFFSETS = 200;
	private static final int MARGIN_NUMBER = 3;
	private static final int SUBIMAGE_NUMBER = 6;
	private static final int GAME_INFO_PANEL_W = 400;
	private static final int GAME_INFO_PANEL_H = 40;
	private static final int TOTAL_ORIENTS = 4;
	private static final int CONSTRIANTS_X = 3;
	private static final int CONSTRIANTS_Y = 4;

	private static final int[][] NEIGHBOR_OFFSETS = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}};
	private static final String IMAGE_FILE = "src/main/resources/Carcassonne.png";
	private static final List<Color> COLORS = new LinkedList<>(
			Arrays.asList(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.WHITE));
	private static final List<String> COLOR_NAME = new LinkedList<>(
			Arrays.asList("red", "blue", "green", "yellow", "white"));

	private final JComboBox<Integer> playerNumBox = new JComboBox<>();
	// game system
	private GameSystem game;
	private HashMap<Integer, BufferedImage> tileImageMap;
	private HashMap<String, Color> playerColorMap = new HashMap<>();
	private HashMap<String, String> playerColorNameMap = new HashMap<>();
	private HashMap<String, BufferedImage> meepleImageMap = new HashMap<>();
	private HashMap<String, Position> meeplePositionMap = new HashMap<>();
	// panel
	private JPanel boardPanel;
	private JScrollPane boardScrollPanel;
	private JPanel playerInfoPanel;
	private JPanel tileInfoPanel;
	private JPanel gameInfoPanel;
	// labels
	private JLabel leftTileNumber;
	private JLabel isPutSuccess;
	private JLabel currPlayerName;
	private JLabel playerScores;
	private JLabel playerLeftMeeples;
	private JLabel playerColor;
	private Tile currTile;

	// tile panel
	private JLabel currTilePic = new JLabel();
	private JLabel meeplePosLabel = new JLabel("Please select meeple Position:");
	private JButton rotateButton = new JButton("rotate");
	private JComboBox<MeeplePosition> meeplePosBox = new JComboBox<>(MeeplePosition.values());
	private BufferedImage currTileImage = null;
	private BufferedImage rotatedImage = null;
	private OrientType tileOrient = OrientType.UP;

	// boardPanel
	private JButton[][] boardButtons;
	private SpringLayout springLayout;

	/**
	 * constructor for game panel. Register for game change listener, load images for each type
	 * of tile, initialize all the JLabels and Jbuttons in game system.
	 * @param game  game system in core implements
	 * @throws IOException if error occurs in loading tile images
	 */
	public GamePanel(GameSystem game) throws IOException {

		this.game = game;
		this.game.addGameChangeListener(this);

		// load images for each tile
		getTileTypeImages(IMAGE_FILE);

		// initialize board panel
		springLayout = new SpringLayout();
		boardPanel = new JPanel();
		boardPanel.setLayout(springLayout);
		boardScrollPanel = new JScrollPane(boardPanel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		setAutoscrolls(true);

		currPlayerName = new JLabel("current player");
		playerScores = new JLabel("Scores");
		playerLeftMeeples = new JLabel("left meeples");
		playerColor = new JLabel("player colors:");

		leftTileNumber = new JLabel("remaining tile number:");
		isPutSuccess = new JLabel("This put tile operation is: ");

		boardButtons = new JButton[GameBoard.BOARD_SIZE][GameBoard.BOARD_SIZE];

		initStartPanel();
	}

	/**
	 * init start panel, user has to choose player number and hit confirm to start game.
	 */
	private void initStartPanel() {
		GridBagLayout lay = new GridBagLayout();
		setLayout(lay);
		JLabel label = new JLabel("Please select player number");
		for (int i = GameSystem.MIN_PLAYERS; i <= GameSystem.MAX_PLAYERS; i++) {
			playerNumBox.addItem(i);
		}
		JButton confirmButton = new JButton("confirm");
		confirmButton.addActionListener(event -> {
			int numPlayers = (int)playerNumBox.getSelectedItem();
			removeAll();
			revalidate();
			repaint();
			initGamePanel(numPlayers);
		});
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.weightx = CONSTRIANTS_X;
		constraints.weighty = CONSTRIANTS_Y;
		addToGrid(label, constraints, 0, 0, CONSTRIANTS_Y, 1);
		addToGrid(playerNumBox, constraints, 0, 1, 2, 1);
		addToGrid(confirmButton, constraints, 0, 2, 1, 1);
	}

	/**
	 * initialize game panel, including board panel, player information panel, game information
	 * panel and tile information panel.
	 * @param numPlayers num of players
	 */
	private void initGamePanel(int numPlayers) {
		setLayout(new BorderLayout());
		// initialize player information panel
		initPlayerPanel();

		// initialize tile panel
		initTilePanel();

		// initialize game info panel
		initGameInfoPanel();

		add(playerInfoPanel, BorderLayout.EAST);
		add(tileInfoPanel, BorderLayout.NORTH);
		add(boardScrollPanel, BorderLayout.CENTER);
		add(gameInfoPanel, BorderLayout.SOUTH);



		// initialize game system
		createButtonOnPanel(GameBoard.BOARD_SIZE - GameBoard.BOARD_SIZE / 2, GameBoard.BOARD_SIZE / 2);
		game.startNewGame(numPlayers, true);

		// init player colors
		initPlayerColors(numPlayers);

		// get first tile
		currTile = game.pickTileFromStack();
	}

	/**
	 * initialize game info panel, including a label for remaining tile number in the game,
	 * and whether the latest move by player is successful or not.
	 */
	private void initGameInfoPanel() {
		gameInfoPanel = new JPanel();
		gameInfoPanel.setLayout(new BoxLayout(gameInfoPanel, BoxLayout.X_AXIS));
		gameInfoPanel.setPreferredSize(new Dimension(GAME_INFO_PANEL_W, GAME_INFO_PANEL_H));
		gameInfoPanel.add(leftTileNumber);
		gameInfoPanel.add(isPutSuccess);
	}

	/**
	 * initialize player info panel, including player names, player scores, and meeple colors
	 * that assigned to the player.
	 */
	private void initPlayerPanel() {
		playerInfoPanel = new JPanel();
		playerInfoPanel.setLayout(new BoxLayout(playerInfoPanel, BoxLayout.PAGE_AXIS));
		// playerInfoPanel.setPreferredSize(new Dimension(PLAYER_INFO_PANEL_W, PLAYER_INFO_PANEL_H));
		playerInfoPanel.add(currPlayerName);
		playerInfoPanel.add(playerScores);
		playerInfoPanel.add(playerLeftMeeples);
		playerInfoPanel.add(playerColor);
	}


	/**
	 * initialize tile information panel, including the image of new tile, a rotate button,
	 * a combo box for choosing meeple positions.
	 */
	private void initTilePanel() {
		tileInfoPanel = new JPanel();
		// currTilePic.setText("current new Tile");
		tileInfoPanel.setLayout(new BoxLayout(tileInfoPanel, BoxLayout.X_AXIS));
		tileInfoPanel.add(currTilePic);
		tileInfoPanel.add(rotateButton);
		tileInfoPanel.add(meeplePosLabel);
		tileInfoPanel.add(meeplePosBox);

		rotateButton.addActionListener(event -> {
			// rotate tile clockwise
			if (currTileImage == null) { return; }
			rotatedImage = rotateImage(rotatedImage, 1);
			tileOrient = OrientType.getOrientById((tileOrient.getOrientType() + 1) % TOTAL_ORIENTS);

			// System.out.println(tileOrient);
			currTilePic.setIcon(new ImageIcon(rotatedImage));
		});
	}


	/**
	 * create a button on board panel, add an action listener, when the button is hit,
	 * the current new tile should be placed on game panel.
	 * @param x x axis
	 * @param y y axis
	 */
	private void createButtonOnPanel(int x, int y) {
		// this button is occupied, return
		if (boardButtons[x][y] != null) { return; }

		boardButtons[x][y] = new JButton();
		boardButtons[x][y].setPreferredSize(new Dimension(TILE_IMAGE_SIZE, TILE_IMAGE_SIZE));

		// add action listener
		boardButtons[x][y].addActionListener(event -> {
			// System.out.println("x:" + x + "y:" + y);
			MeeplePosition mPos = (MeeplePosition)meeplePosBox.getSelectedItem();
			if (mPos == MeeplePosition.NOT_PLACED) {mPos = null;}
			// OrientType o = OrientType.getOrientById((4 - tileOrient.getOrientType()) % 4);
			OrientType o = tileOrient;
			Position newPos = new Position(y, GameBoard.BOARD_SIZE - x);
			if (game.placeTile(currTile, newPos, o, mPos)) {
				// System.out.println("success");
				isPutSuccess.setText("      This put tile operation is success.");
				meeplePosBox.setSelectedIndex(0);
				currTile = game.pickTileFromStack();
				tileOrient = OrientType.UP;
			} else {
				// System.out.println("fail");
				isPutSuccess.setText("       This put tile operation fails.");
			}
		});

		boardPanel.add(boardButtons[x][y]);
		adjustBoardSize();
	}

	// get called when a new random new tile is assigned
	@Override
	public void newTileChanged(Tile tile) {
		// System.out.println("new tile changed");
		// put new tile image on the label
		currTile = tile;
		currTileImage = tileImageMap.get(currTile.getTileType().getTileId());
		rotatedImage = currTileImage;
		currTilePic.setIcon(new ImageIcon(currTileImage));

		// get tile stack size
		int tilesLeft = game.getTilesLeft();
		leftTileNumber.setText("  remaining tiles:  " + tilesLeft + "     ");
	}


	// get called when current player is changed every round
	@Override
	public void playerChanged(String playerName){
		currPlayerName.setText("<html>current Pleyer: " + playerName + "<br></html>");
	}

	// get called when player scores are changed
	@Override
	public void scoreChanged(Map<String, Integer> scores) {
		playerScores.setText("<html>Player Scores:<br> " + map2String(scores) + "<br></html>");
	}

	// get called when player's meeple number is changed
	@Override
	public void meepleNumberChanged(Map<String, Integer> meeples) {
		playerLeftMeeples.setText("<html>left meeples:<br>  " + map2String(meeples) + "<br></html>");
	}

	// get called when game board is changed
	@Override
	public void boardChanged(Tile tile, Position pos, String playerName, String meepleId, MeeplePosition mPos) {
		int x = pos.getX();
		int y = pos.getY();
		// System.out.println(String.format("Put res: row: %d, col: %d", x, y));
		JButton button = boardButtons[x][y];
		this.currTile = tile;

		if (mPos != null && mPos != MeeplePosition.NOT_PLACED) {
			// System.out.println("1 pos " + pos);
			meeplePositionMap.put(meepleId, pos);
		}

		putImageOnButton(button, meepleId, playerName, mPos);

		// create its neigbour buttons
		for (int i = 0; i < NEIGHBOR_OFFSETS.length; i++) {
			int newX = x + NEIGHBOR_OFFSETS[i][0];
			int newY = y + NEIGHBOR_OFFSETS[i][1];
			if (boardButtons[newX][newY] != null) { continue; }
			createButtonOnPanel(newX, newY);
		}
	}

	// changes made when meeples returned
	@Override
	public void returnMeepleChanged(Set<Meeple> meeples) {
		for (Meeple m: meeples) {
			Player player = m.getPlayer();
			int id = m.getId();
			String meepleId = player.getName() + "_" + id;
			// System.out.println("2 meepleId " + meepleId);
			BufferedImage image = meepleImageMap.get(meepleId);
			Position pos = meeplePositionMap.get(meepleId);
			// System.out.println("2 pos " + pos);
			JButton button = boardButtons[pos.getX()][pos.getY()];

			// reset to orginal image, remove meeple
			button.setIcon(new ImageIcon(image));
		}
	}

	// get called when game ends
	@Override
	public void endGameChanged(List<String> winners) {
		JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
		JOptionPane.showMessageDialog(frame, "winner is: " + winners.toString());
		removeAll();
		repaint();
	}

	// put tile image with specifies orientation and meeple position on this button
	private void putImageOnButton(JButton button, String meepleId, String playerName, MeeplePosition mPos) {
		if (rotatedImage == null) {
			rotatedImage = tileImageMap.get(currTile.getTileType().getTileId());
		}

		if (mPos == MeeplePosition.NOT_PLACED || mPos == null) {
			button.setIcon(new ImageIcon(rotatedImage));
			return;
		}

		meepleImageMap.put(meepleId, rotatedImage);

		// set meeple color and position
		Color color = playerColorMap.get(playerName);
		int[] offsets = getMeeplePixelOffset(mPos);
		int xOffset = offsets[0];
		int yOffset = offsets[1];
		rotatedImage = withCircle(rotatedImage, color, xOffset, yOffset, MEEPLE_SIZE);

		// display new image
		button.setIcon(new ImageIcon(rotatedImage));
	}


	// This method returns a new instance of BufferedImage with a circle centered at the provided x
	//  and y. All other contents of the returned image are identical to src.
	private static BufferedImage withCircle(BufferedImage src, Color color, int x, int y, int radius) {
		BufferedImage dest = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		Graphics2D g = (Graphics2D) dest.getGraphics();
		g.drawImage(src, 0, 0, null);
		g.setColor(color);
		g.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
		g.dispose();
		return dest;
	}

	// This method returns a new instance of BufferedImage representing the source image
	// after applying n 90-degree clockwise rotations.
	private BufferedImage rotateImage(BufferedImage src, int n) {
		int weight = src.getWidth();
		int height = src.getHeight();
		AffineTransform at = AffineTransform.getQuadrantRotateInstance(n, weight / 2.0, height / 2.0);
		AffineTransformOp op = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

		BufferedImage dest = new BufferedImage(weight, height, src.getType());
		op.filter(src, dest);
		return dest;
	}


	// add component to grid layout
	private void addToGrid(Component c,GridBagConstraints constraints,int x, int y, int w, int h) {
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = w;
		constraints.gridheight = h;
		add(c, constraints);
	}

	// resize game board is too many tiles
	private void adjustBoardSize() {
		int down = 0, up = GameBoard.BOARD_SIZE;
		int right = 0, left = GameBoard.BOARD_SIZE;

		// go through game board, find four boundaries
		for (int i = 0; i < GameBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < GameBoard.BOARD_SIZE; j++) {
				if (boardButtons[i][j] == null) {continue;}
				down = Math.max(down, i);
				up = Math.min(up, i);
				right = Math.max(right, j);
				left = Math.min(left, j);
			}
		}

		// resize board panel according to current boundary
		int newHeight = Math.max(MIN_PANEL_SIZE, (down - up + MARGIN_NUMBER) * TILE_IMAGE_SIZE + MARGIN_OFFSETS);
		int newWidth = Math.max(MIN_PANEL_SIZE, (right - left + MARGIN_NUMBER) * TILE_IMAGE_SIZE + MARGIN_OFFSETS);
		boardPanel.setPreferredSize(new Dimension(newHeight, newWidth));

		// set constriants in spring layout
		Position center = new Position((down + up) / 2, (right + left) / 2);
		setConstraintsInSpring(center);
	}


	// get meeple pixel offset
	private int[] getMeeplePixelOffset(MeeplePosition mPos) {
		int[] offsets = new int[2];
		int halfSize = TILE_IMAGE_SIZE / 2;
		if (mPos == MeeplePosition.MID) {
			offsets[0] = halfSize;
			offsets[1] = halfSize;
		} else if (mPos == MeeplePosition.UP) {
			offsets[0] = halfSize;
			offsets[1] = MEEPLE_SIZE;
		} else if (mPos == MeeplePosition.LEFT) {
			offsets[0] = MEEPLE_SIZE;
			offsets[1] = halfSize;
		} else if (mPos == MeeplePosition.RIGHT) {
			offsets[0] = TILE_IMAGE_SIZE - MEEPLE_SIZE;
			offsets[1] = halfSize;
		} else if (mPos == MeeplePosition.DOWN) {
			offsets[0] = halfSize;
			offsets[1] = TILE_IMAGE_SIZE - MEEPLE_SIZE;
		}
		return offsets;
	}

	// get tile images from image file
	private void getTileTypeImages(String imageFile) throws IOException {
		tileImageMap = new HashMap<>();
		BufferedImage image = ImageIO.read(new File(imageFile));
		for (int i = 0; i < TOTAL_TILE_TYPES; i++) {
			int y = (i / SUBIMAGE_NUMBER) * TILE_IMAGE_SIZE;
			int x = (i % SUBIMAGE_NUMBER) * TILE_IMAGE_SIZE;
			BufferedImage tileImage = image.getSubimage(x, y, TILE_IMAGE_SIZE, TILE_IMAGE_SIZE);
			tileImageMap.put(i, tileImage);
		}
	}


	// set constraints in spring layout
	private void setConstraintsInSpring(Position center) {
		int centerX = center.getX();
		int centerY = center.getY();
		String hCenter = SpringLayout.HORIZONTAL_CENTER;
		String vCenter = SpringLayout.VERTICAL_CENTER;

		for (int i = 0; i < GameBoard.BOARD_SIZE; i++) {
			for (int j = 0; j < GameBoard.BOARD_SIZE; j++) {
				if (boardButtons[i][j] == null) {continue;}
				JButton button = boardButtons[i][j];
				int xPad = TILE_IMAGE_SIZE * (i - centerX);
				int yPad = TILE_IMAGE_SIZE * (j - centerY);
				springLayout.putConstraint(hCenter, button, yPad, hCenter, boardPanel);
				springLayout.putConstraint(vCenter, button, xPad, vCenter, boardPanel);
			}
		}
	}

	// convert map to string representation
	private String map2String(Map<String, Integer> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Integer> entry: map.entrySet()) {
			String playerName = entry.getKey();
			Integer number = entry.getValue();
			sb.append(playerName);
			sb.append(":  ");
			sb.append(number);
			sb.append(" <br>");
		}
		return sb.toString();
	}

	// convert map to string representation
	private String colorMap2String(Map<String, String> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry: map.entrySet()) {
			String playerName = entry.getKey();
			String color = entry.getValue();
			sb.append(playerName);
			sb.append(":  ");
			sb.append(color);
			sb.append(" <br>");
		}
		return sb.toString();
	}

	// init player colors
	private void initPlayerColors(int numPlayers) {
		List<Player> players = game.getPlayers();
		for(int i = 0; i < numPlayers; i++) {
			Color color = COLORS.get(i);
			String colorName = COLOR_NAME.get(i);
			String playerName = players.get(i).getName();
			playerColorMap.put(playerName, color);
			playerColorNameMap.put(playerName, colorName);
		}
		playerColor.setText("<html>colors:<br>" + colorMap2String(playerColorNameMap));
	}
}
