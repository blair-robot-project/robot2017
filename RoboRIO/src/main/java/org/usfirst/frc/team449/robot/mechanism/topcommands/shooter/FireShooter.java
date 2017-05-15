package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands.TurnMotorOn;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.FixedInActuatedStop;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * Command group for firing the shooter.
 * Runs flywheel, runs static intake, stops dynamic intake, raises intake, and runs feeder.
 */
public class FireShooter extends CommandGroup {
	/**
	 * Constructs a FireShooter command group
	 *
	 * @param sfs    shooter subsystem
	 * @param intake intake subsystem
	 * @param feeder feeder subsystem
	 */
	public FireShooter(SingleFlywheelShooter sfs, Intake2017 intake, FeederSubsystem feeder) {
		if (sfs != null) {
			addParallel(new TurnMotorOn(sfs));
		}
		if (intake != null) {
			addParallel(new FixedInActuatedStop(intake));
		}
		if (feeder != null) {
			addParallel(new TurnMotorOn(feeder));
		}
	}
}
