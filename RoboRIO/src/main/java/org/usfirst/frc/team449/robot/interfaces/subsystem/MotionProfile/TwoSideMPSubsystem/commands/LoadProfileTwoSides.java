package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
public class LoadProfileTwoSides extends Command {

	private TwoSideMPSubsystem subsystem;

	private MotionProfileData left, right;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public LoadProfileTwoSides(TwoSideMPSubsystem subsystem, MotionProfileData left, MotionProfileData right) {
		this.subsystem = subsystem;
		this.left = left;
		this.right = right;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("LoadProfileTwoSides init.", this.getClass());
	}

	/**
	 * Do the state change.
	 */
	@Override
	protected void execute() {
		subsystem.loadMotionProfile(left, right);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("LoadProfileTwoSides end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("LoadProfileTwoSides Interrupted!", this.getClass());
	}
}