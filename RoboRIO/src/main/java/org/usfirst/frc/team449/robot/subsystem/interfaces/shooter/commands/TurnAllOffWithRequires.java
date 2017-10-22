package org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.SubsystemShooter;

/**
 * Turn off the shooter and feeder, using requires() to interrupt any other commands that may be telling them to
 * continue running.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnAllOffWithRequires <T extends YamlSubsystem & SubsystemShooter> extends TurnAllOff {

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	@JsonCreator
	public TurnAllOffWithRequires(@NotNull @JsonProperty(required = true) T subsystem) {
		super(subsystem);
		requires(subsystem);
	}
}