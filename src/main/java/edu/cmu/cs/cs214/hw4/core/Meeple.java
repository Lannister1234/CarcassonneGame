package edu.cmu.cs.cs214.hw4.core;

/**
 * This is class for Meeple in game. A meeple instance has it owner, its id
 * its position in a tile after been placed. a meeple can be marked placed or
 * be marked as return back.
 */
public class Meeple {
	private int id;
	private MeeplePosition mPos;
	private boolean hasPlaced;
	private Player player;

	/**
	 * Init a meeple instance, set its owner, id, mark it is not been placed.
	 * @param player owner of this meeple
	 * @param id     the id for this meeple
	 */
	Meeple(Player player, int id) {
		this.id = id;
		this.player = player;
		this.hasPlaced = false;
		this.mPos = MeeplePosition.NOT_PLACED;
	}

	/**
	 * set position in tile for this meeple.
	 * @param mPos meeple position
	 */
	void setMeeplePos(MeeplePosition mPos) {
		this.mPos = mPos;
	}

	/**
	 * get position in tile for this meeple.
	 * @return meeple position
	 */
	MeeplePosition getMeeplePos() {
		return this.mPos;
	}

	/**
	 * get owner of this meeple.
	 * @return owner
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * mark this meeple as placed.
	 */
	void place() {
		assert !hasPlaced;
		hasPlaced = true;
	}

	/**
	 * mark this meeple as returned back.
	 */
	void getBack() {
		assert hasPlaced;
		hasPlaced = false;
		this.player.returnMeeple();
	}

	/**
	 * whether this meeple has been placed
	 * @return true/ false
	 */
	boolean isPlaced() {
		return hasPlaced;
	}

	/**
	 * return meeple id
	 * @return true/ false
	 */
	public int getId() {
		return this.id;
	}


	/**
	 * String representation for this meeple.
	 * @return string
	 */
	@Override
	public String toString() {
		return player.getName() + "_" + id;
	}

}
