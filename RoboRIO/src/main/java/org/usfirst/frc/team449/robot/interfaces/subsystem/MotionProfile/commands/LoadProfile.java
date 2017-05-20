package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
public class LoadProfile extends Command {

	private CANTalonMPSubsystem subsystem;

	private String profile;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public LoadProfile(CANTalonMPSubsystem subsystem, String profile) {
		this.subsystem = subsystem;
		this.profile = profile;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("LoadProfile init.", this.getClass());
	}

	/**
	 * Do the state change.
	 */
	@Override
	protected void execute() {
		subsystem.loadMotionProfile(profile);
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
		Logger.addEvent("LoadProfile end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("LoadProfile Interrupted!", this.getClass());
	}
}