package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Toggle whether or not to use the NavX to drive straight.
 */
public class ToggleOverrideNavX extends ReferencingCommand {

	public ToggleOverrideNavX(TalonClusterDrive drive) {
		super(drive);
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		System.out.println("OverrideNavX init");
	}

	@Override
	protected void execute() {
		//Toggle whether or not we're overriding the NavX
		((TalonClusterDrive) subsystem).overrideNavX = !((TalonClusterDrive) subsystem).overrideNavX;
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("OverrideNavX end");
	}

	@Override
	protected void interrupted() {
		System.out.println("OverrideNavX Interrupted!");
	}
}

