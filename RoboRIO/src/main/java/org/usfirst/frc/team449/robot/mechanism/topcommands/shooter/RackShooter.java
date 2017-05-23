package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands.SetIntakeMode;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.SpinUpShooter;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

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
	 */
	public RackShooter(SingleFlywheelShooter sfs, Intake2017 intake) {
		if (sfs != null) {
			addParallel(new SpinUpShooter(sfs));
		}
		if (intake != null) {
			addParallel(new SolenoidReverse(intake));
			addParallel(new SetIntakeMode(intake, IntakeSubsystem.IntakeMode.IN_SLOW));
		}
	}
}
