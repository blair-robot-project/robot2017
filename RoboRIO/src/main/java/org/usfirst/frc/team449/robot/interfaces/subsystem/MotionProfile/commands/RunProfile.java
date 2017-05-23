package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.util.BooleanWrapper;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * Loads and runs the given profile into the given subsystem.
 */
public class RunProfile extends CommandGroup {

	/**
	 * The subsystem to execute this command on.
	 */
	private CANTalonMPSubsystem subsystem;

	/**
	 * The profile to run.
	 */
	private MotionProfileData profile;

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
	 * @param profile The motion profile to load and execute.
	 * @param timeout The maximum amount of time this command is allowed to take, in seconds.
	 * @param finishFlag A BooleanWrapper used to signal when this command finishes.
	 */
	public RunProfile(CANTalonMPSubsystem subsystem, MotionProfileData profile, double timeout, BooleanWrapper finishFlag) {
		this.subsystem = subsystem;
		this.profile = profile;
		this.timeout = timeout;
		this.finishFlag = finishFlag;
	}

	/**
	 * Schedule the commands to load and run the profile.
	 */
	@Override
	public void initialize() {
		addSequential(new LoadProfile(subsystem, profile));
		addSequential(new RunLoadedProfile(subsystem, timeout, finishFlag, true));
	}
}
