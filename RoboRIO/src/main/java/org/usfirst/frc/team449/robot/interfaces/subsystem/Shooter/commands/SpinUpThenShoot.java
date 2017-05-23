package org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.WaitForMillis;

/**
 * Spin up the shooter until it's at the target speed, then start feeding in balls.
 */
public class SpinUpThenShoot extends CommandGroup {

	/**
	 * The subsystem to execute this command on.
	 */
	private ShooterSubsystem subsystem;

	/**
	 * Default constructor.
	 * @param subsystem The subsystem to execute this command on.
	 */
	public SpinUpThenShoot(ShooterSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Schedule the commands to spin up the shooter and turn on the feeder.
	 */
	@Override
	public void initialize(){
		addSequential(new SpinUpShooter(subsystem));
		//Use a wait command here because SpinUpShooter is instantaneous.
		addSequential(new WaitForMillis(subsystem.getSpinUpTimeMillis()));
		addSequential(new TurnAllOn(subsystem));
	}
}
