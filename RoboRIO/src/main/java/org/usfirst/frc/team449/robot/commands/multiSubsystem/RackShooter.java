package org.usfirst.frc.team449.robot.commands.multiSubsystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.SubsystemIntake;
import org.usfirst.frc.team449.robot.subsystem.interfaces.intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.SubsystemShooter;
import org.usfirst.frc.team449.robot.subsystem.interfaces.shooter.commands.SpinUpShooter;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.SubsystemSolenoid;
import org.usfirst.frc.team449.robot.subsystem.interfaces.solenoid.commands.SolenoidReverse;

/**
 * Command group for preparing the shooter to fire. Starts flywheel, runs static intake, stops dynamic intake, raises
 * intake, and stops feeder.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RackShooter <T extends SubsystemIntake & SubsystemSolenoid> extends YamlCommandGroupWrapper {

	/**
	 * Constructs a RackShooter command group
	 *
	 * @param subsystemShooter shooter subsystem. Can be null.
	 * @param subsystemIntake  intake subsystem. Can be null.
	 */
	@JsonCreator
	public RackShooter(@Nullable SubsystemShooter subsystemShooter,
	                   @Nullable T subsystemIntake) {
		if (subsystemShooter != null) {
			addParallel(new SpinUpShooter(subsystemShooter));
		}
		if (subsystemIntake != null) {
			addParallel(new SolenoidReverse(subsystemIntake));
			addParallel(new SetIntakeMode(subsystemIntake, SubsystemIntake.IntakeMode.IN_SLOW));
		}
	}
}
