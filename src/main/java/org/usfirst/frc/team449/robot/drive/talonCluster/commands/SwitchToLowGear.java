package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * A wrapper command that switches to low gear.
 */
public class SwitchToLowGear extends ReferencingCommand {

	/**
	 * Default constructor
	 * @param subsystem The subsystem to execute this command on
	 */
	public SwitchToLowGear(MappedSubsystem subsystem) {
		super(subsystem);
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("SwitchToLowGear init.");
	}

	/**
	 * Switch to low gear
	 */
	@Override
	protected void execute() {
		((TalonClusterDrive) subsystem).setLowGear(true);
	}

	/**
	 * Finish immediately because this is a state-change command.
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
		System.out.println("SwitchToLowGear end.");
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("SwitchToLowGear Interrupted!");
	}
}