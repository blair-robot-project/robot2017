package org.usfirst.frc.team449.robot.mechanism.topcommands.shooter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.feeder.commands.RunFeeder;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin.SIDS;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.AccelerateFlywheel;

/**
 * Created by ryant on 2017-02-18.
 */
public class FireShooter extends CommandGroup {
	public FireShooter(SingleFlywheelShooter sfs, Intake2017 intake, FeederSubsystem feeder) {
		requires(intake);

		addParallel(new AccelerateFlywheel(sfs, 2.5 * 60)); // TODO remove useless timeout

		addParallel(new SIDS(intake));

		addParallel(new RunFeeder(feeder));
	}
}
