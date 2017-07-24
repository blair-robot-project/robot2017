package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.SubsystemShooter;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.TurnAllOff;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.SubsystemSolenoid;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;

/**
 * Command group to reset everything. Turns everything off, raises intake
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ResetShooter <T extends SubsystemIntake & SubsystemSolenoid> extends YamlCommandGroupWrapper {

	/**
	 * Constructs a ResetShooter command group
	 *
	 * @param subsystemShooter shooter subsystem. Can be null.
	 * @param intakeSubsystem  intake subsystem. Can be null.
	 */
	@JsonCreator
	public ResetShooter(@Nullable SubsystemShooter subsystemShooter,
	                    @Nullable T intakeSubsystem) {
		if (subsystemShooter != null) {
			addParallel(new TurnAllOff(subsystemShooter));
		}
		if (intakeSubsystem != null) {
			addParallel(new SolenoidReverse(intakeSubsystem));
			addParallel(new SetIntakeMode(intakeSubsystem, SubsystemIntake.IntakeMode.OFF));
		}
	}
}
