package org.usfirst.frc.team449.robot.mechanism.feeder.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Stop the feeder.
 */
public class StopFeeder extends ReferencingCommand {

	/**
	 * The feeder subsystem to execute this command on.
	 */
	private FeederSubsystem feeder;

	/**
	 * Default constructor.
	 *
	 * @param feeder The feeder subsystem to execute this command on.
	 */
	public StopFeeder(FeederSubsystem feeder) {
		super(feeder);
		requires(feeder);
		this.feeder = feeder;
		Logger.addEvent("StopFeeder constructed", this.getClass());
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("StopFeeder init", this.getClass());
	}

	/**
	 * Stop the motor
	 */
	@Override
	protected void execute() {
		feeder.stopVictor();
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
		Logger.addEvent("StopFeeder end", this.getClass());
	}

	/**
	 * Stop the motor and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		feeder.stopVictor();
		Logger.addEvent("StopFeeder interrupted, stopping feeder.", this.getClass());
	}

}
