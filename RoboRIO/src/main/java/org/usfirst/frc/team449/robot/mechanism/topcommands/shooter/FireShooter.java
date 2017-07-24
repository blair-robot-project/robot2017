package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.TurnAllOn;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;

/**
 * Command group for firing the shooter. Runs flywheel, runs static intake, stops dynamic intake, raises intake, and
 * runs feeder.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FireShooter extends YamlCommandGroupWrapper {

	/**
	 * Constructs a FireShooter command group
	 *
	 * @param shooterSubsystem shooter subsystem. Can be null.
	 * @param intakeSubsystem  intake subsystem. Can be null.
	 */
	@JsonCreator
	public FireShooter(@Nullable ShooterSubsystem shooterSubsystem,
	                   @Nullable IntakeSubsystem intakeSubsystem) {
		if (shooterSubsystem != null) {
			addParallel(new TurnAllOn(shooterSubsystem));
		}
		if (intakeSubsystem != null) {
			addParallel(new SetIntakeMode(intakeSubsystem, IntakeSubsystem.IntakeMode.IN_SLOW));
		}
	}
}
