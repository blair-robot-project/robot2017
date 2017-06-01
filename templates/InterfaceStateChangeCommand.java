package org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.BinaryMotorSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
public class InterfaceStateChangeCommand extends Command {

	private InterfaceSubsystem subsystem;

	/**
	 * Default constructor
	 * @param subsystem The subsystem to execute this command on.
	 */
	public InterfaceStateChangeCommand(InterfaceSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("InterfaceStateChangeCommand init.", this.getClass());
	}

	/**
	 * Do the state change.
	 */
	@Override
	protected void execute() {
		subsystem.doAThing();
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
		Logger.addEvent("InterfaceStateChangeCommand end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("InterfaceStateChangeCommand Interrupted!", this.getClass());
	}
}