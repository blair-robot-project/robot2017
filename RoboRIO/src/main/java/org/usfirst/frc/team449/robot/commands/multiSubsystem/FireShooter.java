package org.usfirst.frc.team449.robot.commands.multiSubsystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.SubsystemShooter;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.commands.TurnAllOn;

/**
 * Command group for firing the shooter. Runs flywheel, runs static intake, stops dynamic intake, raises intake, and
 * runs feeder.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FireShooter extends YamlCommandGroupWrapper {

	/**
	 * Constructs a FireShooter command group
	 *
	 * @param subsystemShooter shooter subsystem. Can be null.
	 * @param subsystemIntake  intake subsystem. Can be null.
	 */
	@JsonCreator
	public FireShooter(@Nullable SubsystemShooter subsystemShooter,
	                   @Nullable SubsystemIntake subsystemIntake) {
		if (subsystemShooter != null) {
			addParallel(new TurnAllOn(subsystemShooter));
		}
		if (subsystemIntake != null) {
			addParallel(new SetIntakeMode(subsystemIntake, SubsystemIntake.IntakeMode.IN_SLOW));
		}
	}
}
