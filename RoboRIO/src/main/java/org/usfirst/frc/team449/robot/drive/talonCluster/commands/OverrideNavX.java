package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Set whether or not to use the NavX to drive straight.
 */
//TODO Make this take a NavxSubsystem instead.
public class OverrideNavX extends ReferencingCommand {

	private boolean override;

	/**
	 * Default constructor.
	 * @param drive The drive subsystem to execute this command on
	 */
	public OverrideNavX(TalonClusterDrive drive, boolean override) {
		super(drive);
		requires(subsystem);
		this.override = override;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("OverrideNavX init");
	}

	/**
	 * Set whether or not we're overriding the NavX
	 */
	@Override
	protected void execute() {
		((TalonClusterDrive) subsystem).overrideNavX = override;
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

