package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.feeder.commands.StopFeeder;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.StaticStopDynamicStop;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.updown.IntakeDown;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.DecelerateFlywheel;

/**
 * Command group for loading the shooter.
 * Turns everything off, lowers intake
 */
public class ResetShooter extends CommandGroup {
	/**
	 * Constructs a ResetShooter command group
	 *
	 * @param sfs    shooter subsystem
	 * @param intake intake subsystem
	 * @param feeder feeder subsystem
	 */
	public ResetShooter(SingleFlywheelShooter sfs, Intake2017 intake, FeederSubsystem feeder) {
		requires(intake);
		addParallel(new DecelerateFlywheel(sfs, 5));
		addParallel(new IntakeDown(intake));
		addParallel(new StaticStopDynamicStop(intake));
		addParallel(new StopFeeder(feeder));
	}
}
