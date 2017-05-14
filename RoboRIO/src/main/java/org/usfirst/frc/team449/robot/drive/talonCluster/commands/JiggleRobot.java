package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import maps.org.usfirst.frc.team449.robot.util.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.util.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Created by Noah Gleason on 4/6/2017.
 */
public class JiggleRobot extends CommandGroup {
	/**
	 * Instantiate the ReferencingCommandGroup
	 *
	 * @param subsystem The unidirectionalDrive to execute this command on.
	 */
	public JiggleRobot(UnidirectionalDrive subsystem, ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID turnPID) {
		addSequential(new NavXRelativeTTA(turnPID, 10, subsystem, 3));
		addSequential(new NavXRelativeTTA(turnPID, -10, subsystem, 3));
	}
}
