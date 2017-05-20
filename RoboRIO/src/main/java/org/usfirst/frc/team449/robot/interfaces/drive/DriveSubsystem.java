package org.usfirst.frc.team449.robot.interfaces.drive;

/**
 * Created by noah on 5/20/17.
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
