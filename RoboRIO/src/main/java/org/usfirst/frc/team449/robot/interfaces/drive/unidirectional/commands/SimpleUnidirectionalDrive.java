package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.UnidirectionalOI;

/**
 * Very simple arcade drive control.
 */
public class SimpleUnidirectionalDrive extends Command {

	/**
	 * The OI used for input.
	 */
	public UnidirectionalOI oi;
	
	private UnidirectionalDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param drive The drive to execute this command on
	 * @param oi    The OI that gives the input to this command.
	 */
	public SimpleUnidirectionalDrive(UnidirectionalDrive drive, UnidirectionalOI oi) {
		requires((Subsystem) drive);
		this.oi = oi;
		this.subsystem = drive;
		//Default commands need to require their subsystems.
	}

	/**
	 * Stop the drive for safety reasons.
	 */
	@Override
	protected void initialize() {
		subsystem.fullStop();
	}

	/**
	 * Give output to the motors based on the stick inputs.
	 */
	@Override
	protected void execute() {
		//Calculate the right and left outputs from the fwd and rot inputs.
		subsystem.setOutput(oi.getLeftOutput(), oi.getRightOutput());
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
		System.out.println("SimpleUnidirectionalDrive Interrupted! Stopping the robot.");
		//Brake for safety!
		subsystem.fullStop();
	}
}
