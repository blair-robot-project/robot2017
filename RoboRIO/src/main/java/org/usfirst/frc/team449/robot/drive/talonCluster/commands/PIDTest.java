package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Drive forward at constant speed then stop to tune PID.
 */
public class PIDTest extends ReferencingCommandGroup {

	/**
	 * Default constructor
	 * @param mappedSubsystem the TalonClusterDrive to execute this command on
	 */
	public PIDTest(MappedSubsystem mappedSubsystem) {
		super(mappedSubsystem);
		requires(mappedSubsystem);

		TalonClusterDrive driveSubsystem = (TalonClusterDrive) mappedSubsystem;

		//Drive forward for a bit
		addSequential(new DriveAtSpeed(driveSubsystem, 0.5, 2.5));
		//Stop
		addSequential(new DriveAtSpeed(driveSubsystem, 0, 10));
	}
}
