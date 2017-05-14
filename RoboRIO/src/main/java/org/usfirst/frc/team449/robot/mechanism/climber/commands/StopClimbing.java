package org.usfirst.frc.team449.robot.mechanism.climber.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Interrupts the current climb command and manually stops the climber.
 */
public class StopClimbing extends ReferencingCommand {

	/**
	 * The climber to execute this command on
	 */
	private ClimberSubsystem climber;

	/**
	 * Default constructor
	 *
	 * @param climber The climber subsystem to execute this command on
	 */
	public StopClimbing(ClimberSubsystem climber) {
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
	 * Stop the climb motor.
	 */
	@Override
	protected void execute() {
		climber.setPercentVbus(0);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Stop the motor and log that the command has ended.
	 */
	@Override
	protected void end() {
		//Stop climbing for safety
		climber.setPercentVbus(0);
		Logger.addEvent("StopClimbing end", this.getClass());
	}

	/**
	 * Stop the motor and log that the command has been interrupted.
	 */
	@Override
	protected void interrupted() {
		//Stop climbing for safety
		climber.setPercentVbus(0);
		Logger.addEvent("StopClimbing interrupted, stopping climb.", this.getClass());
	}

}
