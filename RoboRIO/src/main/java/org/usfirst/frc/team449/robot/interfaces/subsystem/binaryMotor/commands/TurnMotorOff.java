package org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.BinaryMotorSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Turns off the motor of the specified subsystem.
 */
public class TurnMotorOff extends Command {

	/**
	 * The subsystem to execute this command on.
	 */
	private BinaryMotorSubsystem subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public TurnMotorOff(BinaryMotorSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("TurnMotorOff init.", this.getClass());
	}

	/**
	 * Turn the motor off.
	 */
	@Override
	protected void execute() {
		subsystem.turnMotorOff();
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
		Logger.addEvent("TurnMotorOff end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("TurnMotorOff Interrupted!", this.getClass());
	}
}