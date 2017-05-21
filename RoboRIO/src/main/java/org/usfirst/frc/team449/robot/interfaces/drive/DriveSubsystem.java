package org.usfirst.frc.team449.robot.interfaces.drive;

/**
 * Any locomotion device for the robot.
 */
public interface DriveSubsystem {
	/**
	 * Completely stop the robot by setting the voltage to each side to be 0.
	 */
	void fullStop();

	/**
	 * If this drive uses motors that can be disabled, enable them.
	 */
	void enableMotors();
}
