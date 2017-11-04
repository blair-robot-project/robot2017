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
 * Command group for intaking balls from the ground. Stops flywheel, runs static intake, runs dynamic intake, lowers
 * intake, and stops feeder.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class LoadShooter <T extends SubsystemIntake & SubsystemSolenoid> extends YamlCommandGroupWrapper {

	/**
	 * Constructs a LoadShooter command group
	 *
	 * @param subsystemShooter shooter subsystem. Can be null.
	 * @param subsystemIntake  intake subsystem. Can be null.
	 */
	@JsonCreator
	public LoadShooter(@Nullable SubsystemShooter subsystemShooter,
	                   @Nullable T subsystemIntake) {
		if (subsystemShooter != null) {
			addParallel(new TurnAllOff(subsystemShooter));
		}
		if (subsystemIntake != null) {
			addParallel(new SolenoidReverse(subsystemIntake));
			addParallel(new SetIntakeMode(subsystemIntake, SubsystemIntake.IntakeMode.IN_FAST));
		}
	}
}
