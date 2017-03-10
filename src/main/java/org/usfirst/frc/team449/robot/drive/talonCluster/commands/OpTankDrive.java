package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.TankOI;

/**
 * Very simple tank drive control.
 */
public class OpTankDrive extends ReferencingCommand {

	/**
	 * The OI used for input.
	 */
	public TankOI oi;

	/**
	 * The output to be given to the left side of the robot.
	 */
	private double leftThrottle;

	/**
	 * The output to be given to the right side of the robot.
	 */
	private double rightThrottle;

	/**
	 * Default constructor.
	 * @param drive The drive to execute this command on.
	 * @param oi The OI this command gets input from.
	 */
	public OpTankDrive(TalonClusterDrive drive, TankOI oi) {
		super(drive);
		this.oi = oi;
		//Default commands must require their subsystem.
		requires(subsystem);
		System.out.println("Drive Robot bueno");
	}

	/**
	 * Stop the drive for safety reasons.
	 */
	@Override
	protected void initialize() {
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	/**
	 * Give output to the motors based on the stick inputs.
	 */
	@Override
	protected void execute() {
		//Get the throttle from the OI
		rightThrottle = oi.getRightThrottle();
		leftThrottle = oi.getLeftThrottle();

		//Give output to the motors.
		((TalonClusterDrive) subsystem).setDefaultThrottle(leftThrottle, rightThrottle);

		//Data logging
		((TalonClusterDrive) subsystem).logData();
	}

	/**
	 * Run constantly because this is a default drive
	 * @return false
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Log and brake when interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("OpTankDrive Interrupted! Stopping the robot.");
		//Stop if interrupted for safety.
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
