package edu.cmu.cs.cs214.hw4.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is enum class for orient type. can be up, left, down, right.
 */
public enum OrientType {
	UP(0), LEFT(1), DOWN(2), RIGHT(3);
	private static final List<OrientType> ORIENT_TYPE_LIST =
			Collections.unmodifiableList(Arrays.asList(UP, LEFT, DOWN, RIGHT));
	private final int type;

	/**
	 * Constructor for orient type.
	 * @param type type id
	 */
	OrientType(int type) {
		this.type = type;
	}

	/**
	 * get orient type by type id.
	 * @param id type id
	 * @return orient type
	 */
	public static OrientType getOrientById(int id) {
		return ORIENT_TYPE_LIST.get(id);
	}

	/**
	 * Get orient type id.
	 * @return id
	 */
	public int getOrientType() {
		return this.type;
	}
}
