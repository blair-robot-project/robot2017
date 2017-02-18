package org.usfirst.frc.team449.robot.mechanism.feeder.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

/**
 * Activate the feeder.
 */
public class RunFeeder extends ReferencingCommand {

	FeederSubsystem feeder;

	public RunFeeder(FeederSubsystem feeder) {
		super(feeder);
		requires(feeder);
		this.feeder = feeder;
		System.out.println("RunFeeder constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("RunFeeder init");
	}

	@Override
	protected void execute() {
		feeder.runVictor();
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("RunFeeder end");
	}

	@Override
	protected void interrupted() {
		feeder.stopVictor();
		System.out.println("RunFeeder interrupted, stopping feeder.");
	}

}
