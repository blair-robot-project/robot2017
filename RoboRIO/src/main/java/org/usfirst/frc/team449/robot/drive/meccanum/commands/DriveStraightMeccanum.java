package org.usfirst.frc.team449.robot.drive.meccanum.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.meccanum.MeccanumDrive;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Sets all four wheels to move at the same speed.
 * Created by sam on 1/26/17.
 */
public class DriveStraightMeccanum extends ReferencingCommand {

	public OISubsystem oi;
	public double leftThrottle;

	public DriveStraightMeccanum(MeccanumDrive drive, OISubsystem oi) {
		super(drive);
		this.oi = oi;
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		((MeccanumDrive) subsystem).setDefaultThrottle(0);
	}

	@Override
	protected void execute() {
		leftThrottle = oi.getDriveAxisLeft();
		((MeccanumDrive) subsystem).setDefaultThrottle(leftThrottle);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
