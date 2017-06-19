package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.usfirst.frc.team449.robot.interfaces.drive.DriveSubsystem;

/**
 * A drive with a left side and a right side. "Unidirectional" because it can only move forwards or backwards, not
 * sideways.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_OBJECT, property="@class")
public interface UnidirectionalDrive extends DriveSubsystem {

	/**
	 * Set the output of each side of the drive.
	 *
	 * @param left  The output for the left side of the drive, from [-1, 1]
	 * @param right the output for the right side of the drive, from [-1, 1]
	 */
	void setOutput(double left, double right);

	/**
	 * Get the velocity of the left side of the drive.
	 * @return The signed velocity in rotations per second.
	 */
	double getLeftVel();

	/**
	 * Get the velocity of the right side of the drive.
	 * @return The signed velocity in rotations per second.
	 */
	double getRightVel();
}
