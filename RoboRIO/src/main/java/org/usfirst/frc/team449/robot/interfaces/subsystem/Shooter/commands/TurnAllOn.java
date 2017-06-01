package org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Turn on the shooter and the feeder.
 */
public class TurnAllOn extends Command {

	/**
	 * The subsystem to execute this command on.
	 */
	private ShooterSubsystem subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public TurnAllOn(ShooterSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("TurnAllOn init.", this.getClass());
	}

	/**
	 * Turn on the shooter and feeder.
	 */
	@Override
	protected void execute() {
		subsystem.turnFeederOn();
		subsystem.turnShooterOn();
		subsystem.setShooterState(ShooterSubsystem.ShooterState.SHOOTING);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("TurnAllOn end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("TurnAllOn Interrupted!", this.getClass());
	}
}