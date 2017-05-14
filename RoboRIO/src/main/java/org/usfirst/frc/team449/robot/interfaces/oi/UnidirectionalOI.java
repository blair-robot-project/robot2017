package org.usfirst.frc.team449.robot.interfaces.oi;

/**
 * An OI to control a robot with a unidirectional drive that has a left and right side (e.g. not meccanum, swerve, or holonomic)
 */
public interface UnidirectionalOI extends BaseOI{

	/**
	 * The output to be given to the left side of the drive.
	 * @return Output to left side from [-1, 1]
	 */
	double getLeftOutput();

	/**
	 * The output to be given to the right side of the drive.
	 * @return Output to right side from [-1, 1]
	 */
	double getRightOutput();

	/**
	 * Map all buttons to commands. Should only be run after all subsystems have been instantiated.
	 */
	void mapButtons();
}
