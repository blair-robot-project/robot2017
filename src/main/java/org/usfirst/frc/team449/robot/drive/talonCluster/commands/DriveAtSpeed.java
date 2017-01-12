package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Created by BlairRobot on 2017-01-12.
 */
public class DriveAtSpeed extends ReferencingCommand{

	double speed;

	public DriveAtSpeed(TalonClusterDrive drive, double speed) {
		super(drive);
		requires(subsystem);
		this.speed = speed;
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void execute() {
		((TalonClusterDrive) subsystem).setDefaultThrottle(speed, speed);
		((TalonClusterDrive) subsystem).logData(speed);
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
		System.out.println("DriveAtSpeed Interrupted! Stopping the robot.");
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
