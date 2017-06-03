package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.TurnAllOff;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SolenoidSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;

/**
 * Command group to reset everything.
 * Turns everything off, raises intake
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class ResetShooter extends CommandGroup {
	/**
	 * Constructs a ResetShooter command group
	 *
	 * @param shooterSubsystem shooter subsystem
	 * @param intakeSubsystem  intake subsystem. Must also be a {@link SolenoidSubsystem}.
	 */
	@JsonCreator
	public ResetShooter(@JsonProperty(required = true) ShooterSubsystem shooterSubsystem,
	                    @JsonProperty(required = true) IntakeSubsystem intakeSubsystem) {
		if (shooterSubsystem != null) {
			addParallel(new TurnAllOff(shooterSubsystem));
		}
		if (intakeSubsystem != null) {
			addParallel(new SolenoidReverse((SolenoidSubsystem) intakeSubsystem));
			addParallel(new SetIntakeMode(intakeSubsystem, IntakeSubsystem.IntakeMode.OFF));
		}
	}
}
