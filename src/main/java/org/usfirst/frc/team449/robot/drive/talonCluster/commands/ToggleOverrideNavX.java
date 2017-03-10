package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Toggle whether or not to use the NavX to drive straight.
 */
//TODO Make this take a NavxSubsystem instead.
public class ToggleOverrideNavX extends ReferencingCommand {

	/**
	 * Default constructor.
	 * @param drive The drive subsystem to execute this command on
	 */
	public ToggleOverrideNavX(TalonClusterDrive drive) {
		super(drive);
		requires(subsystem);
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("OverrideNavX init");
	}

	/**
	 * Toggle whether or not we're overriding the NavX
	 */
	@Override
	protected void execute() {
		((TalonClusterDrive) subsystem).overrideNavX = !((TalonClusterDrive) subsystem).overrideNavX;
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
		System.out.println("OverrideNavX end");
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("OverrideNavX Interrupted!");
	}
}

