package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands.TurnMotorOff;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.FixedStopActuatedStop;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * Command group to reset everything.
 * Turns everything off, raises intake
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
		if (sfs != null) {
			addParallel(new TurnMotorOff(sfs));
		}
		if (intake != null) {
			addParallel(new SolenoidReverse(intake));
			addParallel(new FixedStopActuatedStop(intake));
		}
		if (feeder != null) {
			addParallel(new TurnMotorOff(feeder));
		}
	}
}
