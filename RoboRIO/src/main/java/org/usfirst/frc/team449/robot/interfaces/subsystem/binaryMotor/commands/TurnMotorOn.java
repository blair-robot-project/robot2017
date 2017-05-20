package org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.BinaryMotorSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
public class TurnMotorOn extends Command {

	private BinaryMotorSubsystem subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public TurnMotorOn(BinaryMotorSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("TurnMotorOn init.", this.getClass());
	}

	/**
	 * Do the state change.
	 */
	@Override
	protected void execute() {
		subsystem.turnMotorOn();
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
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("TurnMotorOn end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("TurnMotorOn Interrupted!", this.getClass());
	}
}