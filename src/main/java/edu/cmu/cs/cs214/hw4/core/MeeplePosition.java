package edu.cmu.cs.cs214.hw4.core;

/**
 * enum class for meeple position, can be not_placed, up, left, down, right
 * and mid (only for monastery).
 */
public enum MeeplePosition {
	NOT_PLACED(-1), UP(0), LEFT(1), DOWN(2), RIGHT(3), MID(4);
	private final int pos;

	/**
	 * Constructor for meeple position.
	 * @param pos position id
	 */
	MeeplePosition(int pos) { this.pos = pos; }

	/**
	 * Get position id for this meeple position.
	 * @return position id
	 */
	public int getMeeplePos() { return this.pos; }
}

