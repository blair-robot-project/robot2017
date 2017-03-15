package org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois;

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
