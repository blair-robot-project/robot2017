package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import maps.org.usfirst.frc.team449.robot.util.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;

/**
 * Rotates the robot back and forth in order to dislodge any stuck balls.
 */
public class JiggleRobot extends CommandGroup {

	/**
	 * The drive to execute this command on.
	 */
	private UnidirectionalDrive subsystem;

	/**
	 * The map containing the PID constants to control the angular PID loop.
	 */
	private ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID PID;

	/**
	 * Instantiate the ReferencingCommandGroup
	 *
	 * @param subsystem The unidirectionalDrive to execute this command on.
	 * @param turnPID The map with the PID constants for the angular loop.
	 */
	public JiggleRobot(UnidirectionalDrive subsystem, ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID turnPID) {
		this.subsystem = subsystem;
		PID = turnPID;
	}

	/**
	 * Schedule the commands to turn back and forth.
	 */
	@Override
	public void initialize(){
		addSequential(new NavXRelativeTTA(PID, 10, subsystem, 3));
		addSequential(new NavXRelativeTTA(PID, -10, subsystem, 3));
	}
}
