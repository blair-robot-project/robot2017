package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional;

/**
 * Created by noah on 5/13/17.
 */
public interface UnidirectionalDrive {

	/**
	 * Set the output of each side of the drive.
	 * @param left The output for the left side of the drive, from [-1, 1]
	 * @param right the output for the right side of the drive, from [-1, 1]
	 */
	void setOutput(double left, double right);

	/**
	 * Completely stop the robot by setting the voltage to each side to be 0.
	 */
	void fullStop();
}
