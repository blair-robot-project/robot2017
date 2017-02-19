package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.feeder.commands.RunFeeder;
import org.usfirst.frc.team449.robot.mechanism.feeder.commands.StopFeeder;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.DynamicIntakeIn;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.SIDI;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.SIDS;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.StaticIntakeStop;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.updown.IntakeUp;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.AccelerateFlywheel;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.DecelerateFlywheel;

/**
 * Created by ryant on 2017-02-18.
 */
public class RackShooter extends CommandGroup {
	public RackShooter(SingleFlywheelShooter sfs, Intake2017 intake, FeederSubsystem feeder) {
		addParallel(new AccelerateFlywheel(sfs, 5));

		addParallel(new IntakeUp(intake));
		addParallel(new SIDS(intake));

		addParallel(new StopFeeder(feeder));
	}
}
