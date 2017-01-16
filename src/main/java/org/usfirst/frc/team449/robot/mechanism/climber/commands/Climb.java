package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;

/**
 * Created by Justin on 1/12/2017.
 */
public class Climb extends ReferencingCommand {

	ClimberSubsystem climber;

	public Climb(ClimberSubsystem climber) {
		super(climber);
		requires(climber);
		this.climber = climber;
		System.out.println("Climb constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("Climb init");
	}

	@Override
	protected void execute() {
		climber.setPercentVbus(1);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		climber.setPercentVbus(0);
		System.out.println("Climb end");
	}

	@Override
	protected void interrupted() {
		climber.setPercentVbus(0);
		System.out.println("Climb interrupted, stopping climb.");
	}

}
