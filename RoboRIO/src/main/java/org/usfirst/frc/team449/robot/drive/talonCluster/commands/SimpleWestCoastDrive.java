package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.UnidirectionalOI;

/**
 * Very simple arcade drive control.
 */
public class SimpleWestCoastDrive extends ReferencingCommand {

	/**
	 * The OI used for input.
	 */
	public UnidirectionalOI oi;
	
	private TalonClusterDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param drive The drive to execute this command on
	 * @param oi    The OI that gives the input to this command.
	 */
	public SimpleWestCoastDrive(TalonClusterDrive drive, UnidirectionalOI oi) {
		super(drive);
		this.oi = oi;
		this.subsystem = drive;
		//Default commands need to require their subsystems.
		requires(subsystem);
	}

	/**
	 * Stop the drive for safety reasons.
	 */
	@Override
	protected void initialize() {
		subsystem.setDefaultThrottle(0.0, 0.0);
	}

	/**
	 * Give output to the motors based on the stick inputs.
	 */
	@Override
	protected void execute() {
		//Calculate the right and left outputs from the fwd and rot inputs.
		subsystem.setDefaultThrottle(oi.getLeftOutput(), oi.getRightOutput());

		//Logging.
		subsystem.logData();
	}

	/**
	 * Run constantly because this is a default drive
	 *
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
		System.out.println("SimpleWestCoastDrive Interrupted! Stopping the robot.");
		//Brake for safety!
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}
}
