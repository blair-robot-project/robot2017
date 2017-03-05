package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Created by Noah Gleason on 2/12/2017.
 */
public class OverrideNavX extends ReferencingCommand{

	public OverrideNavX(TalonClusterDrive drive){
		super(drive);
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		System.out.println("OverrideNavX init");
	}

	@Override
	protected void execute() {
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

