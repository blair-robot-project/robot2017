package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * Program created by noah on 1/11/17.
 */
@Deprecated
public class PIDTune extends ReferencingCommandGroup {
	/**
	 * Instantiate the ReferencingCommandGroup
	 *
	 * @param mappedSubsystem the {@link MappedSubsystem} to feed to this {@code ReferencingCommandGroup}'s
	 *                        {@link ReferencingCommand}s
	 */
	public PIDTune(MappedSubsystem mappedSubsystem) {
		super(mappedSubsystem);
		SingleFlywheelShooter flywheelSubsystem = (SingleFlywheelShooter) mappedSubsystem;

		for (int i = 0; i < 5; i++) {
			addSequential(new AccelerateFlywheel(flywheelSubsystem, 5));
			addSequential(new DecelerateFlywheel(flywheelSubsystem, 5));
		}
	}
}
