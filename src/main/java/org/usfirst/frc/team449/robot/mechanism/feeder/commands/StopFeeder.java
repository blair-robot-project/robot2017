package org.usfirst.frc.team449.robot.mechanism.feeder.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;

/**
 * Stop the feeder.
 */
public class StopFeeder extends ReferencingCommand {

	/**
	 * The feeder subsystem this controls.
	 */
	private FeederSubsystem feeder;

	public StopFeeder(FeederSubsystem feeder) {
		super(feeder);
		requires(feeder);
		this.feeder = feeder;
		System.out.println("StopFeeder constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("StopFeeder init");
	}

	@Override
	protected void execute() {
		feeder.stopVictor();
	}

	@Override
	protected boolean isFinished() {
		//Runs instantaneously
		return true;
	}

	@Override
	protected void end() {
		System.out.println("StopFeeder end");
	}

	@Override
	protected void interrupted() {
		feeder.stopVictor();
		System.out.println("StopFeeder interrupted, stopping feeder.");
	}

}
