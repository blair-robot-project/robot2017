package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * Loads and runs the given profiles into the given subsystem.
 */
public class RunProfileTwoSides extends CommandGroup {

	/**
	 * Default constructor.
	 * @param subsystem The subsystem to execute this command on.
	 * @param left The motion profile for the left side to load and execute.
	 * @param right The motion profile for the right side to load and execute.
	 * @param timeout The maximum amount of time this command is allowed to take, in seconds..
	 */
	public RunProfileTwoSides(TwoSideMPSubsystem subsystem, MotionProfileData left, MotionProfileData right, double timeout) {
		addSequential(new LoadProfileTwoSides(subsystem, left, right));
		addSequential(new RunLoadedProfile(subsystem, timeout, true));
	}
}
