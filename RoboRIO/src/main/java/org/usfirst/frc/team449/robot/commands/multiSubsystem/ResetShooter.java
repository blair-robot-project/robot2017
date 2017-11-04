package org.usfirst.frc.team449.robot.commands.multiSubsystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.SubsystemShooter;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.commands.TurnAllOff;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.commands.SolenoidReverse;

/**
 * Command group to reset everything. Turns everything off, raises intake
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ResetShooter <T extends SubsystemIntake & SubsystemSolenoid> extends YamlCommandGroupWrapper {

	/**
	 * Constructs a ResetShooter command group
	 *
	 * @param subsystemShooter shooter subsystem. Can be null.
	 * @param subsystemIntake  intake subsystem. Can be null.
	 */
	@JsonCreator
	public ResetShooter(@Nullable SubsystemShooter subsystemShooter,
	                    @Nullable T subsystemIntake) {
		if (subsystemShooter != null) {
			addParallel(new TurnAllOff(subsystemShooter));
		}
		if (subsystemIntake != null) {
			addParallel(new SolenoidReverse(subsystemIntake));
			addParallel(new SetIntakeMode(subsystemIntake, SubsystemIntake.IntakeMode.OFF));
		}
	}
}
