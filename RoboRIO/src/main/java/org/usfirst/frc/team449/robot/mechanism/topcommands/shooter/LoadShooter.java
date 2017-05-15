package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands.TurnMotorOff;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.FixedInActuatedIn;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * Command group for intaking balls from the ground.
 * Stops flywheel, runs static intake, runs dynamic intake, lowers intake, and stops feeder.
 */
public class LoadShooter extends CommandGroup {
	/**
	 * Constructs a LoadShooter command group
	 *
	 * @param sfs    shooter subsystem
	 * @param intake intake subsystem
	 * @param feeder feeder subsystem
	 */
	public LoadShooter(SingleFlywheelShooter sfs, Intake2017 intake, FeederSubsystem feeder) {
		if (sfs != null) {
			addParallel(new TurnMotorOff(sfs));
		}
		if (intake != null) {
			addParallel(new SolenoidReverse(intake));
			addParallel(new FixedInActuatedIn(intake));
		}
		if (feeder != null) {
			addParallel(new TurnMotorOff(feeder));
		}
	}
}
