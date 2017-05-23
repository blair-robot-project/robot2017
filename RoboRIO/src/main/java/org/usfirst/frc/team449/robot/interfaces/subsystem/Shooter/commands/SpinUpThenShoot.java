package org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.WaitForMillis;

/**
 * Created by noah on 5/20/17.
 */
public class SpinUpThenShoot extends CommandGroup {

	private ShooterSubsystem subsystem;

	public SpinUpThenShoot(ShooterSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	@Override
	public void initialize(){
		addSequential(new SpinUpShooter(subsystem));
		addSequential(new WaitForMillis(subsystem.getSpinUpTimeMillis()));
		addSequential(new TurnAllOn(subsystem));
	}
}
