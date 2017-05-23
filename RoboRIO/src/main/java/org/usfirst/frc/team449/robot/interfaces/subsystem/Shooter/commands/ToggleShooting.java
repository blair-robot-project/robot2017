package org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;

/**
 * Toggle whether or not the subsystem is firing.
 */
public class ToggleShooting extends CommandGroup {


	/**
	 * Default constructor.
	 * @param subsystem The subsystem to execute this command on.
	 */
	public ToggleShooting(ShooterSubsystem subsystem) {
		switch (subsystem.getShooterState()) {
			case OFF:
				addSequential(new SpinUpThenShoot(subsystem));
				break;
			case SHOOTING:
				addSequential(new TurnAllOff(subsystem));
				break;
			case SPINNING_UP:
				addSequential(new TurnAllOn(subsystem));
		}
	}
}
