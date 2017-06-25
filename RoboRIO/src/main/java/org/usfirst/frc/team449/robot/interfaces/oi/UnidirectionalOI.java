package org.usfirst.frc.team449.robot.interfaces.oi;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An OI to control a robot with a unidirectional drive that has a left and right side (e.g. not meccanum, swerve, or
 * holonomic)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface UnidirectionalOI {

	/**
	 * The output to be given to the left side of the drive.
	 *
	 * @return Output to left side from [-1, 1]
	 */
	double getLeftOutput();

	/**
	 * The output to be given to the right side of the drive.
	 *
	 * @return Output to right side from [-1, 1]
	 */
	double getRightOutput();
}
