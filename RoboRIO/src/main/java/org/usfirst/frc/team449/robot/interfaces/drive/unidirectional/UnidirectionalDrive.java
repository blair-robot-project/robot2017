package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional;

import org.usfirst.frc.team449.robot.interfaces.drive.DriveSubsystem;

/**
 * Created by noah on 5/13/17.
 */
public interface UnidirectionalDrive extends DriveSubsystem{

	/**
	 * Set the output of each side of the drive.
	 * @param left The output for the left side of the drive, from [-1, 1]
	 * @param right the output for the right side of the drive, from [-1, 1]
	 */
	void setOutput(double left, double right);
}
