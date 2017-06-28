package org.usfirst.frc.team449.robot.interfaces.oi;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An arcade-style dual joystick OI.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public abstract class ArcadeOI implements UnidirectionalOI {

	/**
	 * Get the rotational input.
	 *
	 * @return rotational velocity component from [-1, 1], where 1 is right and -1 is left.
	 */
	public abstract double getRot();

	/**
	 * Get the velocity input.
	 *
	 * @return forward velocity component from [-1, 1], where 1 is forwards and -1 is backwards
	 */
	public abstract double getFwd();

	/**
	 * The output to be given to the left side of the drive.
	 *
	 * @return Output to left side from [-1, 1]
	 */
	public double getLeftOutput() {
		return getFwd() + getRot();
	}

	/**
	 * The output to be given to the right side of the drive.
	 *
	 * @return Output to right side from [-1, 1]
	 */
	public double getRightOutput() {
		return getFwd() - getRot();
	}

	/**
	 * Whether the driver is trying to drive straight.
	 *
	 * @return True if the driver is trying to drive straight, false otherwise.
	 */
	@Override
	public boolean commandingStraight() {
		return getRot() == 0;
	}
}
