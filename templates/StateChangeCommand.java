package org.usfirst.frc.team449.template;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
public class StateChangeCommand extends ReferencingCommand{

	/**
	 * Default constructor
	 * @param subsystem The subsystem to execute this command on.
	 */
	public StateChangeCommand(MappedSubsystem subsystem) {
		super(subsystem);
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("StateChangeCommand init.", this.getClass());
	}

	/**
	 * Do the state change.
	 */
	@Override
	protected void execute() {
		//subsystem.doThing();
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
		Logger.addEvent("StateChangeCommand end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("StateChangeCommand Interrupted!", this.getClass());
	}
}