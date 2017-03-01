package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;

/**
 * Created by Noah Gleason on 2/11/2017.
 */
public class ManualClimb extends ReferencingCommand {

	ClimberSubsystem climber;

	public ManualClimb(ClimberSubsystem climber) {
		super(climber);
		requires(climber);
		this.climber = climber;
		System.out.println("CurrentClimb constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("CurrentClimb init");
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
		System.out.println("CurrentClimb end");
	}

	@Override
	protected void interrupted() {
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb interrupted, stopping climb.");
	}

}
