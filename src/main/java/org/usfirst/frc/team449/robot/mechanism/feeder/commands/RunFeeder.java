package org.usfirst.frc.team449.robot.mechanism.feeder.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;

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
	 * @param feeder The feeder subsystem to execute this command on.
	 */
	public RunFeeder(FeederSubsystem feeder) {
		super(feeder);
		requires(feeder);
		this.feeder = feeder;
		System.out.println("RunFeeder constructed");
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("RunFeeder init");
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
		System.out.println("RunFeeder end");
	}

	/**
	 * Stop the motor and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		feeder.stopVictor();
		System.out.println("RunFeeder interrupted, stopping feeder.");
	}

}
