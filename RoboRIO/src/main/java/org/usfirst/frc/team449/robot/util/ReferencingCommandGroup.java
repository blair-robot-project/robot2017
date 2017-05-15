package org.usfirst.frc.team449.robot.util;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;

/**
 * {@link CommandGroup} that has a reference to the
 * subsystem its commands use.
 * <p>
 * Intended for use with {@link ReferencingCommand}s.
 * <p>
 * TODO put this in the central repo
 */
public abstract class ReferencingCommandGroup extends CommandGroup {
	/**
	 * Subsystem to reference to
	 */
	private MappedSubsystem subsystem;

	/**
	 * Instantiate the ReferencingCommandGroup
	 *
	 * @param mappedSubsystem the {@link MappedSubsystem}
	 *                        to feed to this
	 *                        {@code
	 *                        ReferencingCommandGroup}'s
	 *                        {@link ReferencingCommand}s
	 */
	public ReferencingCommandGroup(MappedSubsystem mappedSubsystem) {
		subsystem = mappedSubsystem;
	}
}
