package org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois;

/**
 * Created by ryant on 2017-01-25.
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
