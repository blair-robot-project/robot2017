package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.components.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.feeder.commands.StopFeeder;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.FixedInActuatedStop;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.AccelerateFlywheel;

/**
 * Command group for preparing the shooter to fire.
 * Starts flywheel, runs static intake, stops dynamic intake, raises intake, and stops feeder.
 */
public class RackShooter extends CommandGroup {
	/**
	 * Constructs a RackShooter command group
	 *
	 * @param sfs    shooter subsystem
	 * @param intake intake subsystem
	 * @param feeder feeder subsystem
	 */
	public RackShooter(SingleFlywheelShooter sfs, Intake2017 intake, FeederSubsystem feeder) {
		if (sfs != null) {
			addParallel(new AccelerateFlywheel(sfs, 5));
		}
		if (intake != null) {
			addParallel(new SolenoidReverse(intake));
			addParallel(new FixedInActuatedStop(intake));
		}
		if (feeder != null) {
			addParallel(new StopFeeder(feeder));
		}
	}
}
