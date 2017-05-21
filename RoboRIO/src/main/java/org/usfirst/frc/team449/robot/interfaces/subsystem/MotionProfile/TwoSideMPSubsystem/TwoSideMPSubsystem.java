package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem;

import maps.org.usfirst.frc.team449.robot.util.MotionProfileMap;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * An MP subsystem with two sides that therefore needs two profiles at a time.
 */
public interface TwoSideMPSubsystem extends CANTalonMPSubsystem{
	/**
	 * Loads given profiles into the left and right sides of the drive.
	 * @param left The profile to load into the left side.
	 * @param right The profile to load into the right side.
	 */
	void loadMotionProfile(MotionProfileData left, MotionProfileData right);
}
