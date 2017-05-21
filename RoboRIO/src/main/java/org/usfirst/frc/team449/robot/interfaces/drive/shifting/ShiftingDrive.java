package org.usfirst.frc.team449.robot.interfaces.drive.shifting;

/**
 * A drive that has a high gear and a low gear and can switch between them.
 */
public interface ShiftingDrive {

	/**
	 * Check if we should autoshift, then, if so, shift.
	 */
	void autoshift();

	/**
	 * @return The gear this subsystem is currently in.
	 */
	gear getGear();

	/**
	 * Shift to a specific gear.
	 *
	 * @param gear Which gear to shift to.
	 */
	void setGear(gear gear);

	/**
	 * A getter for whether we're currently overriding autoshifting.
	 * @return true if overriding, false otherwise.
	 */
	boolean getOverrideAutoshift();

	/**
	 * A setter for overriding the autoshifting.
	 * @param override Whether or not to override autoshifting.
	 */
	void setOverrideAutoshift(boolean override);

	enum gear {
		HIGH, LOW
	}
}
