package edu.cmu.cs.cs214.hw4.core;

/**
 * This is the position class.
 */

public class Position {
	private int x;
	private int y;

	/**
	 * Constructor of position class.
	 * @param x x
	 * @param y y
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * get x value.
	 * @return X
	 */
	public int getX() { return this.x; }

	/**
	 * get y value.
	 * @return Y
	 */
	public int getY() { return this.y; }

	/**
	 * Override toString method.
	 * @return string representation
	 */
	@Override
	public String toString() { return "Position: (" + this.x + ", " + this.y + ")"; }

}
