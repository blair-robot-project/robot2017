package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.ArcadeOI;

/**
 * Very simple arcade drive control.
 */
public class OpArcadeDrive extends ReferencingCommand {

	/**
	 * The OI used for input.
	 */
	public ArcadeOI oi;

	/**
	 * The output to give the left side of the drive.
	 */
	private double leftThrottle;

	/**
	 * The output to the right side of the drive.
	 */
	private double rightThrottle;

	/**
	 * Default constructor
	 * @param drive The drive to execute this command on
	 * @param oi The OI that gives the input to this command.
	 */
	public OpArcadeDrive(TalonClusterDrive drive, ArcadeOI oi) {
		super(drive);
		this.oi = oi;
		//Default commands need to require their subsystems.
		requires(subsystem);
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
		//Calculate the right and left outputs from the fwd and rot inputs.
		rightThrottle = oi.getFwd() + oi.getRot();
		leftThrottle = oi.getFwd() - oi.getRot();
		((TalonClusterDrive) subsystem).setDefaultThrottle(leftThrottle, rightThrottle);

		//Logging.
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
		//Brake for safety!
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
