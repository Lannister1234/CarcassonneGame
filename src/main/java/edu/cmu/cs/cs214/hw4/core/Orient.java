package edu.cmu.cs.cs214.hw4.core;

import java.util.Objects;

/**
 * This is class for orient (position + orient type). A feature can have multiple orients.
 * Orient class is used to check match condition for different features.
 * The orient is composed by a position and a orient type. The class should be able
 * to check whether it matches with another orient. For example, if one orient is
 * at ((0, 0), RIGHT), another orient is at ((1, 0) LEFT), these two orients are matched.
 */
public class Orient {
	private Position position;
	private OrientType direction;

	/**
	 * Constructor for orient.
	 * @param pos position
	 * @param direction orient type
	 */
	Orient(Position pos, OrientType direction) {
		this.position = pos;
		this.direction = direction;
	}

	/**
	 * check whether two orients match with each other.
	 * @param other another orient
	 * @return match or not
	 */
	public boolean orientMatch(Orient other) {
		int yOffset = this.position.getY() - other.position.getY();
		int xOffset = this.position.getX() - other.position.getX();

		if (xOffset == 0 && yOffset == 1) {
			return this.direction == OrientType.DOWN && other.direction == OrientType.UP;
		}

		if (xOffset == 0 && yOffset == -1) {
			return this.direction == OrientType.UP && other.direction == OrientType.DOWN;
		}

		if (xOffset == 1 && yOffset == 0) {
			return this.direction == OrientType.LEFT && other.direction == OrientType.RIGHT;
		}

		if (xOffset == -1 && yOffset == 0) {
			return this.direction == OrientType.RIGHT && other.direction == OrientType.LEFT;
		}
		return false;
	}

	/**
	 * Override equal function.
	 * @param other other orient
	 * @return equal or not
	 */
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Orient)) { return false; }
		Orient o = (Orient) other;
		return o.position == this.position && o.direction == this.direction;
	}
	/**
	 * Override hashcode function.
	 * @return hashcode
	 */
	@Override
	public int hashCode() {
		return Objects.hash(this.position, this.direction);
	}

	/**
	 * Override toString function.
	 * @return String
	 */
	@Override
	public String toString() {
		return this.position.toString() + " " + this.direction.name();
	}

}
