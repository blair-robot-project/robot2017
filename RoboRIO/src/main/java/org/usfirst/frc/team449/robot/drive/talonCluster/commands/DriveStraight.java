package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.TankOI;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Drives straight when using a tank drive. Not updated for new OI organization.
 */
//TODO update this to the new OI organization.
public class DriveStraight extends ReferencingCommand {
	private TankOI oi;

	private boolean useLeft;

	private double throttle;

	public DriveStraight(TalonClusterDrive drive, TankOI oi, boolean useLeft) {
		super(drive);
		this.oi = oi;
		this.useLeft = useLeft;
		requires(subsystem);
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void execute() {
		if(useLeft){
			throttle = oi.getLeftThrottle();
		} else {
			throttle = oi.getRightThrottle();
		}
		((TalonClusterDrive) subsystem).setDefaultThrottle(throttle, throttle);
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
