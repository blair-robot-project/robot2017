package org.usfirst.frc.team449.robot.util;

/**
 * A wrapper on a boolean that allows it to be passed to a function and changed within that function. The default Boolean
 * class does NOT do this, so we have to make our own wrapper.
 */
public class BooleanWrapper {

	private boolean value;

	public BooleanWrapper(boolean value) {
		this.value = value;
	}

	public boolean get() {
		return value;
	}

	public void set(boolean value) {
		this.value = value;
	}
}
