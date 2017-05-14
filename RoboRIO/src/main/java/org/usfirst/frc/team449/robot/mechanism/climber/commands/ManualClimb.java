package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Climb the rope until this command is interrupted. WARNING: May damage stuff because of the torque.
 */
public class ManualClimb extends ReferencingCommand {

	/**
	 * The climber to execute this command on
	 */
	private ClimberSubsystem climber;

	/**
	 * Default constructor
	 *
	 * @param climber The climber subsystem to execute this command on
	 */
	public ManualClimb(ClimberSubsystem climber) {
		super(climber);
		requires(climber);
		this.climber = climber;
		Logger.addEvent("PowerClimb constructed", this.getClass());
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("PowerClimb init", this.getClass());
	}

	/**
	 * Climb at full speed.
	 */
	@Override
	protected void execute() {
		//Climb as fast as we can
		climber.setPercentVbus(1);
	}

	/**
	 * Does not stop, another command like {@link StopClimbing} needs to be run to stop it.
	 *
	 * @return false
	 */
	@Override
	protected boolean isFinished() {
		//DOES NOT STOP; you need another command like StopClimbing to interrupt it.
		return false;
	}

	/**
	 * Stop the motor and log that the command has ended.
	 */
	@Override
	protected void end() {
		//Stop climbing for safety
		climber.setPercentVbus(0);
		Logger.addEvent("ManualClimb end", this.getClass());
	}

	/**
	 * Stop the motor and log that the command has been interrupted.
	 */
	@Override
	protected void interrupted() {
		//Stop climbing for safety
		climber.setPercentVbus(0);
		Logger.addEvent("ManualClimb interrupted, stopping climb.", this.getClass());
	}

}
