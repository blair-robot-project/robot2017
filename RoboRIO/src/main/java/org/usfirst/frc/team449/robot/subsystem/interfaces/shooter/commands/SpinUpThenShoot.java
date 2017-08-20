package org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.commands.general.WaitForMillis;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.SubsystemShooter;

/**
 * Spin up the shooter until it's at the target speed, then start feeding in balls.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SpinUpThenShoot extends YamlCommandGroupWrapper {

	/**
	 * Default constructor.
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	@JsonCreator
	public SpinUpThenShoot(@NotNull @JsonProperty(required = true) SubsystemShooter subsystem) {
		addSequential(new SpinUpShooter(subsystem));
		//Use a wait command here because SpinUpShooter is instantaneous.
		addSequential(new WaitForMillis(subsystem.getSpinUpTimeMillis()));
		addSequential(new TurnAllOn(subsystem));
	}
}
