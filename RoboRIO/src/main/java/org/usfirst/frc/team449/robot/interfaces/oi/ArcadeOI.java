package org.usfirst.frc.team449.robot.interfaces.oi;

/**
 * An arcade-style dual joystick OI.
 */
public interface ArcadeOI {
	/**
	 * @return rotational velocity component
	 */
	double getRot();

	/**
	 * @return forward velocity component
	 */
	double getFwd();
}
