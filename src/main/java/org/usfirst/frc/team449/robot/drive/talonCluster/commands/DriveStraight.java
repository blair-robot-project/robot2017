package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Drives straight when using a tank drive. Not updated for new OI organization.
 */
//TODO update this to the new OI organization.
public class DriveStraight extends ReferencingCommand {
	public OISubsystem oi;

	double leftThrottle;
	double rightThrottle;

	public DriveStraight(TalonClusterDrive drive, OISubsystem oi) {
		super(drive);
		this.oi = oi;
		requires(subsystem);
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void execute() {
		leftThrottle = oi.getDriveAxisLeft();
		((TalonClusterDrive) subsystem).setDefaultThrottle(leftThrottle, leftThrottle);
		((TalonClusterDrive) subsystem).logData();
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
	}

	@Override
	protected void interrupted() {
		System.out.println("DriveStraight Interrupted! Stopping the robot.");
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
