package org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;

/**
 * Drive back and forth to tune PID.
 */
public class PIDBackAndForth extends CommandGroup {

	/**
	 * The drive to execute this command on.
	 */
	private UnidirectionalDrive subsystem;

	/**
	 * Instantiate the CommandGroup
	 *
	 * @param subsystem the unidirectional drive to execute this command on.
	 */
	public PIDBackAndForth(UnidirectionalDrive subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Schedule the commands to drive forwards and backwards.
	 */
	@Override
	public void initialize(){
		double time = 1.5;

		//Drive forwards
		addSequential(new DriveAtSpeed(subsystem, 0.7, time));
		//Drive backwards
		addSequential(new DriveAtSpeed(subsystem, -0.7, time));
	}
}
