package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.feeder.commands.StopFeeder;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.DynamicIntakeIn;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.SIDI;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.SIDS;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.StaticIntakeIn;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.updown.IntakeDown;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.DecelerateFlywheel;

/**
 * Created by ryant on 2017-02-18.
 */
public class LoadShooter extends CommandGroup {
	public LoadShooter(SingleFlywheelShooter sfs, Intake2017 intake, FeederSubsystem feeder) {
		requires(intake);
		addParallel(new DecelerateFlywheel(sfs, 5));

		addParallel(new IntakeDown(intake));
		addParallel(new SIDI(intake));

		addParallel(new StopFeeder(feeder));
	}
}
