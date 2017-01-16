package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * {@link CommandGroup} that has a reference to the
 * subsystem its commands use.
 * <p>
 * Intended for use with {@link ReferencingCommand}s.
 * <p>
 * TODO put this in the central repo
 */
public abstract class ReferencingCommandGroup extends
		CommandGroup {
	/**
	 * Subsystem to reference to
	 */
	MappedSubsystem subsystem;

	/**
	 * Instantiate the ReferencingCommandGroup
	 *
	 * @param mappedSubsystem the {@link MappedSubsystem}
	 *                        to feed to this
	 *                        {@code
	 *                        ReferencingCommandGroup}'s
	 *                        {@link ReferencingCommand}s
	 */
	// TODO refactor mappedSubsystem to something SHORTER
	public ReferencingCommandGroup(MappedSubsystem mappedSubsystem) {
		subsystem = mappedSubsystem;
	}
}
