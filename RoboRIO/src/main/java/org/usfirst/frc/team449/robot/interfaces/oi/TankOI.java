package org.usfirst.frc.team449.robot.interfaces.oi;

/**
 * A tank-style dual joystick OI.
 */
public abstract class TankOI implements UnidirectionalOI {
	/**
	 * @return percent of max speed for left motor cluster from [-1.0, 1.0]
	 */
	public abstract double getLeftThrottle();

	/**
	 * @return percent of max speed for right motor cluster from [-1.0, 1.0]
	 */
	public abstract double getRightThrottle();

	/**
	 * The output to be given to the left side of the drive.
	 *
	 * @return Output to left side from [-1, 1]
	 */
	public double getLeftOutput() {
		return getLeftThrottle();
	}

	/**
	 * The output to be given to the right side of the drive.
	 *
	 * @return Output to right side from [-1, 1]
	 */
	public double getRightOutput() {
		return getRightThrottle();
	}
}
