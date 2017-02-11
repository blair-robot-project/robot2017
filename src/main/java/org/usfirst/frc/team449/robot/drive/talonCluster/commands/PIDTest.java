package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Created by BlairRobot on 2017-01-12.
 */
public class PIDTest extends ReferencingCommandGroup {
	/**
	 * Instantiate the ReferencingCommandGroup
	 *
	 * @param mappedSubsystem the {@link MappedSubsystem}
	 *                        to feed to this
	 *                        {@code
	 *                        ReferencingCommandGroup}'s
	 *                        {@link ReferencingCommand}s
	 */
	public PIDTest(MappedSubsystem mappedSubsystem) {
		super(mappedSubsystem);
		requires(mappedSubsystem);

		TalonClusterDrive driveSubsystem = (TalonClusterDrive) mappedSubsystem;

		for (int i = 0; i < 5; i++) {
			addSequential(new DriveAtSpeed(driveSubsystem,  0.6), 2);
			addSequential(new DriveAtSpeed(driveSubsystem, 0), 500);
		}
	}
}
