package org.usfirst.frc.team449.robot.mechanism.feeder.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;

/**
 * Toggle the feeder.
 */
public class ToggleFeeder extends ReferencingCommand {

	/**
	 * The feeder subsystem this controls.
	 */
	private FeederSubsystem feeder;

	public ToggleFeeder(FeederSubsystem feeder) {
		super(feeder);
		requires(feeder);
		this.feeder = feeder;
		System.out.println("ToggleFeeder constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("ToggleFeeder init");
	}

	@Override
	protected void execute() {
		if (feeder.running) {
			feeder.stopVictor();
		} else {
			feeder.runVictor();
		}
	}

	@Override
	protected boolean isFinished() {
		//Runs instantaneously
		return true;
	}

	@Override
	protected void end() {
		System.out.println("ToggleFeeder end");
	}

	@Override
	protected void interrupted() {
		feeder.stopVictor();
		System.out.println("ToggleFeeder interrupted, stopping feeder.");
	}

}
