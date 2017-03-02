package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;

/**
 * Interrupts the current climb command and manually stops the climber.
 */
public class StopClimbing extends ReferencingCommand {

	/**
	 * The climber this is controlling
	 */
	ClimberSubsystem climber;

	public StopClimbing(ClimberSubsystem climber) {
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
		//Stop climbing
		climber.setPercentVbus(0);
	}

	@Override
	protected boolean isFinished() {
		//Finishes instantaneously
		return true;
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
