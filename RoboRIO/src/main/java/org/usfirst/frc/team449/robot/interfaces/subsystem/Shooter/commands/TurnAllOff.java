package org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
public class TurnAllOff extends Command {

	private ShooterSubsystem subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public TurnAllOff(ShooterSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("TurnAllOff init.", this.getClass());
	}

	/**
	 * Do the state change.
	 */
	@Override
	protected void execute() {
		subsystem.turnFeederOff();
		subsystem.turnShooterOff();
		subsystem.setShooterState(ShooterSubsystem.ShooterState.OFF);
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
		Logger.addEvent("TurnAllOff end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("TurnAllOff Interrupted!", this.getClass());
	}
}