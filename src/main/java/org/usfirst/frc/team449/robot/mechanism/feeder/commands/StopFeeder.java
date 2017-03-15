package org.usfirst.frc.team449.robot.mechanism.feeder.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;

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
	 * @param feeder The feeder subsystem to execute this command on.
	 */
	public StopFeeder(FeederSubsystem feeder) {
		super(feeder);
		requires(feeder);
		this.feeder = feeder;
		System.out.println("StopFeeder constructed");
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("StopFeeder init");
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
		System.out.println("StopFeeder end");
	}

	/**
	 * Stop the motor and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		feeder.stopVictor();
		System.out.println("StopFeeder interrupted, stopping feeder.");
	}

}
