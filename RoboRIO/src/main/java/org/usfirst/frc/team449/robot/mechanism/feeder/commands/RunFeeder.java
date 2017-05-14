package org.usfirst.frc.team449.robot.mechanism.feeder.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Activate the feeder.
 */
public class RunFeeder extends ReferencingCommand {

	/**
	 * The feeder subsystem to execute this command on.
	 */
	private FeederSubsystem feeder;

	/**
	 * Default constructor.
	 *
	 * @param feeder The feeder subsystem to execute this command on.
	 */
	public RunFeeder(FeederSubsystem feeder) {
		super(feeder);
		requires(feeder);
		this.feeder = feeder;
		Logger.addEvent("RunFeeder constructed", this.getClass());
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("RunFeeder init", this.getClass());
	}

	/**
	 * Run the motor
	 */
	@Override
	protected void execute() {
		feeder.runVictor();
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
		Logger.addEvent("RunFeeder end", this.getClass());
	}

	/**
	 * Stop the motor and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		feeder.stopVictor();
		Logger.addEvent("RunFeeder interrupted, stopping feeder.", this.getClass());
	}

}
