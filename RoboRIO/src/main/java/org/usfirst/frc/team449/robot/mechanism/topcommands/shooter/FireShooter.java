package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.TurnAllOn;

/**
 * Command group for firing the shooter.
 * Runs flywheel, runs static intake, stops dynamic intake, raises intake, and runs feeder.
 */
public class FireShooter extends CommandGroup {
	/**
	 * Constructs a FireShooter command group
	 *
	 * @param shooterSubsystem shooter subsystem
	 * @param intakeSubsystem  intake subsystem
	 */
	public FireShooter(ShooterSubsystem shooterSubsystem, IntakeSubsystem intakeSubsystem) {
		if (shooterSubsystem != null) {
			addParallel(new TurnAllOn(shooterSubsystem));
		}
		if (intakeSubsystem != null) {
			addParallel(new SetIntakeMode(intakeSubsystem, IntakeSubsystem.IntakeMode.IN_SLOW));
		}
	}
}
