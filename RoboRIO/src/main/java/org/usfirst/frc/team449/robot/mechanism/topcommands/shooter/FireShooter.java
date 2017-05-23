package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.TurnAllOn;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * Command group for firing the shooter.
 * Runs flywheel, runs static intake, stops dynamic intake, raises intake, and runs feeder.
 */
public class FireShooter extends CommandGroup {

	private ShooterSubsystem shooterSubsystem;

	private IntakeSubsystem intakeSubsystem;

	/**
	 * Constructs a FireShooter command group
	 *
	 * @param shooter    shooter subsystem
	 * @param intake intake subsystem
	 */
	public FireShooter(ShooterSubsystem shooter, IntakeSubsystem intake) {
		shooterSubsystem = shooter;
		intakeSubsystem = intake;
	}

	@Override
	public void initialize(){
		if (shooterSubsystem != null) {
			addParallel(new TurnAllOn(shooterSubsystem));
		}
		if (intakeSubsystem != null) {
			addParallel(new SetIntakeMode(intakeSubsystem, IntakeSubsystem.IntakeMode.IN_SLOW));
		}
	}
}
