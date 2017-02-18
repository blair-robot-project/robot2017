package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Created by Noah Gleason on 2/12/2017.
 */
public class OverrideAutoShift extends ReferencingCommand{

	private boolean override;

	public OverrideAutoShift(TalonClusterDrive drive, boolean override){
		super(drive);
		requires(subsystem);
		this.override = override;
	}

	@Override
	protected void initialize() {
		System.out.println("OverrideAutoShift init");
	}

	@Override
	protected void execute() {
		((TalonClusterDrive) subsystem).overrideAutoShift = override;
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("OverrideAutoShift end");
	}

	@Override
	protected void interrupted() {
		System.out.println("OverrideAutoShift Interrupted!");
	}
}

