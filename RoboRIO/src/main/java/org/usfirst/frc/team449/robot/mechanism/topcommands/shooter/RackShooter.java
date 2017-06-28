package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.SpinUpShooter;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SolenoidSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;

/**
 * Command group for preparing the shooter to fire.
 * Starts flywheel, runs static intake, stops dynamic intake, raises intake, and stops feeder.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RackShooter <T extends IntakeSubsystem & SolenoidSubsystem> extends YamlCommandGroupWrapper {

	/**
	 * Constructs a RackShooter command group
	 *
	 * @param shooterSubsystem shooter subsystem. Can be null.
	 * @param intakeSubsystem  intake subsystem. Can be null.
	 */
	@JsonCreator
	public RackShooter(@Nullable ShooterSubsystem shooterSubsystem,
	                   @Nullable T intakeSubsystem) {
		if (shooterSubsystem != null) {
			addParallel(new SpinUpShooter(shooterSubsystem));
		}
		if (intakeSubsystem != null) {
			addParallel(new SolenoidReverse(intakeSubsystem));
			addParallel(new SetIntakeMode(intakeSubsystem, IntakeSubsystem.IntakeMode.IN_SLOW));
		}
	}
}
