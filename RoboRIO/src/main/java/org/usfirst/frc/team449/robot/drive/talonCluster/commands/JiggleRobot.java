package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import maps.org.usfirst.frc.team449.robot.util.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;

/**
 * Rotates the robot back and forth in order to dislodge any stuck balls.
 */
public class JiggleRobot extends CommandGroup {
	/**
	 * Instantiate the CommandGroup
	 *
	 * @param subsystem The unidirectionalDrive to execute this command on.
	 */
	public JiggleRobot(UnidirectionalDrive subsystem, ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID turnPID) {
		addSequential(new NavXRelativeTTA(turnPID, 10, subsystem, 3));
		addSequential(new NavXRelativeTTA(turnPID, -10, subsystem, 3));
	}
}
