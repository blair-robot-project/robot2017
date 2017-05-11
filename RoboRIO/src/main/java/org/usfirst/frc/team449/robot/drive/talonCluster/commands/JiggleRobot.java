package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Created by Noah Gleason on 4/6/2017.
 */
public class JiggleRobot extends ReferencingCommandGroup {
	/**
	 * Instantiate the ReferencingCommandGroup
	 *
	 * @param mappedSubsystem the {@link MappedSubsystem}
	 *                        to feed to this
	 *                        {@code
	 *                        ReferencingCommandGroup}'s
	 *                        {@link ReferencingCommand}s
	 */
	public JiggleRobot(TalonClusterDrive mappedSubsystem) {
		super(mappedSubsystem);
		addSequential(new NavXRelativeTTA(mappedSubsystem.turnPID, 10, mappedSubsystem, 3));
		addSequential(new NavXRelativeTTA(mappedSubsystem.turnPID, -10, mappedSubsystem, 3));
	}
}
