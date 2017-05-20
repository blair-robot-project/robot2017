package org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.WaitForMillis;

/**
 * Created by noah on 5/20/17.
 */
public class SpinUpThenShoot extends CommandGroup {

	public SpinUpThenShoot(ShooterSubsystem subsystem) {
		addSequential(new SpinUpShooter(subsystem));
		addSequential(new WaitForMillis(subsystem.getSpinUpTimeMillis()));
		addSequential(new TurnAllOn(subsystem));
	}
}
