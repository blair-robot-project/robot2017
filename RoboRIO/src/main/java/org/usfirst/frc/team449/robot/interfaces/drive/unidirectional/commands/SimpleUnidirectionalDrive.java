package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.UnidirectionalOI;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Very simple unidirectional drive control.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class SimpleUnidirectionalDrive extends Command {

	/**
	 * The OI used for input.
	 */
	public UnidirectionalOI oi;

	/**
	 * The drive subsystem to execute this command on.
	 */
	private UnidirectionalDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param drive The drive to execute this command on
	 * @param oi    The OI that gives the input to this command.
	 */
	public SimpleUnidirectionalDrive(UnidirectionalDrive drive, UnidirectionalOI oi) {
		this.oi = oi;
		this.subsystem = drive;
		//Default commands need to require their subsystems.
		requires((Subsystem) drive);
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
		Logger.addEvent("SimpleUnidirectionalDrive Interrupted! Stopping the robot.", this.getClass());
		//Brake for safety!
		subsystem.fullStop();
	}
}
