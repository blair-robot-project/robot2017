package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.components.NavxSubsystem;

/**
 * Set whether or not to use the NavX to drive straight.
 */
public class OverrideNavX extends Command {

	private boolean override;
	private NavxSubsystem subsystem;

	/**
	 * Default constructor.
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	public OverrideNavX(NavxSubsystem subsystem, boolean override) {
		this.override = override;
		this.subsystem = subsystem;
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
		subsystem.setOverrideNavX(override);
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

