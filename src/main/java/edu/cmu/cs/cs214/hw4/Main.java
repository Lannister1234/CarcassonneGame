package edu.cmu.cs.cs214.hw4;

import edu.cmu.cs.cs214.hw4.core.GameSystem;
import edu.cmu.cs.cs214.hw4.gui.GamePanel;

import javax.swing.*;
import java.io.IOException;

/**
 * This is main function that starts Carcassonne game GUI.
 */
public class Main {
	private static final String GAME_NAME = "Carcassonne";

	/**
	 * Main function for starting game panel.
	 * @param args args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(Main::createGamePanel);
	}

	// create frame and initialize game panel
	private static void createGamePanel() {
		JFrame frame = new JFrame(GAME_NAME);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		GameSystem game = new GameSystem();
		GamePanel gamePanel;
		try {
			gamePanel = new GamePanel(game);
			gamePanel.setOpaque(true);
			frame.setContentPane(gamePanel);
		} catch (IOException e){
			e.printStackTrace();
		}
		frame.pack();
		frame.setVisible(true);
	}

}
