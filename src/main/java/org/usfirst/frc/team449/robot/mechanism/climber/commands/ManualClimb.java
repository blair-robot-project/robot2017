package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;

/**
 * Climb the rope until this command is interrupted. WARNING: May damage stuff because of the torque.
 */
public class ManualClimb extends ReferencingCommand {

	/**
	 * The climber this is controlling
	 */
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
		//Climb as fast as we can
		climber.setPercentVbus(1);
	}

	@Override
	protected boolean isFinished() {
		//DOES NOT STOP; you need another command like StopClimbing to interrupt it.
		return false;
	}

	@Override
	protected void end() {
		//Stop climbing for safety
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb end");
	}

	@Override
	protected void interrupted() {
		//Stop climbing for safety
		climber.setPercentVbus(0);
		System.out.println("CurrentClimb interrupted, stopping climb.");
	}

}
