package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Go at a certain speed for a set number of seconds
 */
public class DriveAtSpeed extends ReferencingCommand {

	/**
	 * Speed to go at
	 */
	private double speed;

	/**
	 * How long to run for
	 */
	private double seconds;

	/**
	 * When this command was initialized.
	 */
	private long startTime;

	/**
	 * Default constructor
	 * @param drive The drive to execute this command on
	 * @param speed How fast to go, in RPS
	 * @param seconds How long to drive for.
	 */
	public DriveAtSpeed(TalonClusterDrive drive, double speed, double seconds) {
		//Initialize stuff
		super(drive);
		this.speed = speed;
		this.seconds = seconds;
		System.out.println("Drive Robot bueno");
	}

	/**
	 * Set up start time.
	 */
	@Override
	protected void initialize() {
		//Set up start time
		startTime = System.nanoTime();
		//Reset drive speed (for safety reasons)
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	/**
	 * Send output to motors and log data
	 */
	@Override
	protected void execute() {
		//Set the speed
		((TalonClusterDrive) subsystem).setDefaultThrottle(speed, speed);
		//Log data
		((TalonClusterDrive) subsystem).logData(speed);
	}

	/**
	 * Exit after the command's been running for long enough
	 * @return True if timeout has been reached, false otherwise
	 */
	@Override
	protected boolean isFinished() {
		//Time-based exit
		return (System.nanoTime() - startTime) * 1e-9 > seconds;
	}

	/**
	 * Stop the drive when the command ends.
	 */
	@Override
	protected void end() {
		((TalonClusterDrive) subsystem).logData();
		//Brake on exit.
		((TalonClusterDrive) subsystem).setDefaultThrottle(0, 0);
	}

	/**
	 * Log and stop the drive when the command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("DriveAtSpeed Interrupted! Stopping the robot.");
		((TalonClusterDrive) subsystem).logData();
		//Break if we're interrupted
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
