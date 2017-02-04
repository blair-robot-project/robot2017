package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.TankOI;

/**
 * Program created by noah on 1/8/17.
 */
public class OpTankDrive extends ReferencingCommand {
	public TankOI oi;

	private double leftThrottle;
	private double rightThrottle;

	public OpTankDrive(TalonClusterDrive drive, TankOI oi) {
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
		rightThrottle = oi.getRightThrottle();
		leftThrottle = oi.getLeftThrottle();
		((TalonClusterDrive) subsystem).logData();
		((TalonClusterDrive) subsystem).setDefaultThrottle(leftThrottle, rightThrottle);
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
		System.out.println("OpTankDrive Interrupted! Stopping the robot.");
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
