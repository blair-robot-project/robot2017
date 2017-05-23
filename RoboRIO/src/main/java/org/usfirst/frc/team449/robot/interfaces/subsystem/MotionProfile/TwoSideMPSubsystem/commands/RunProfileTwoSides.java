package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.util.BooleanWrapper;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * Loads and runs the given profiles into the given subsystem.
 */
public class RunProfileTwoSides extends CommandGroup {

	/**
	 * The subsystem to execute this command on.
	 */
	private TwoSideMPSubsystem subsystem;

	/**
	 * The profiles for the left and right sides to run, respectively.
	 */
	private MotionProfileData left, right;

	/**
	 * The timeout for this command in seconds.
	 */
	private double timeout;

	/**
	 * The BooleanWrapper used to signal when this command finishes.
	 */
	private BooleanWrapper finishFlag;

	/**
	 * Default constructor.
	 * @param subsystem The subsystem to execute this command on.
	 * @param left The motion profile for the left side to load and execute.
	 * @param right The motion profile for the right side to load and execute.
	 * @param timeout The maximum amount of time this command is allowed to take, in seconds.
	 * @param finishFlag A BooleanWrapper used to signal when this command finishes.
	 */
	public RunProfileTwoSides(TwoSideMPSubsystem subsystem, MotionProfileData left, MotionProfileData right, double timeout, BooleanWrapper finishFlag) {
		this.subsystem = subsystem;
		this.left = left;
		this.right = right;
		this.timeout = timeout;
		this.finishFlag = finishFlag;
	}

	/**
	 * Schedule the commands to load and run the profiles.
	 */
	@Override
	public void initialize() {
		addSequential(new LoadProfileTwoSides(subsystem, left, right));
		addSequential(new RunLoadedProfile(subsystem, timeout, finishFlag, true));
	}
}
