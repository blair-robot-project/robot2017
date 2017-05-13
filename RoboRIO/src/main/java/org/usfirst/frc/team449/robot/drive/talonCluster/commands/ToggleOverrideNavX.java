package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;

/**
 * Toggle whether or not to use the NavX to drive straight.
 */
public class ToggleOverrideNavX extends Command {

	private NavxSubsystem subsystem;

	/**
	 * Default constructor.
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	public ToggleOverrideNavX(NavxSubsystem subsystem) {
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
	 * Toggle whether or not we're overriding the NavX
	 */
	@Override
	protected void execute() {
		subsystem.setOverrideNavX(!subsystem.getOverrideNavX());
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

