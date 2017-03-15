package org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois;

/**
 * A tank-style dual joystick OI.
 */
public interface TankOI {
	/**
	 * @return percent of max speed for left motor cluster (-1.0 to 1.0)
	 */
	double getLeftThrottle();

	/**
	 * @return percent of max speed for right motor cluster (-1.0 to 1.0)
	 */
	double getRightThrottle();
}
