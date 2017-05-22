package org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.BinaryMotorSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Turns off the motor of the subsystem, but does so while using requires() to interrupt any other commands currently controlling the subsystem.
 */
public class TurnMotorOffWithRequires extends Command {

	/**
	 * The subsystem to execute this command on.
	 */
	private BinaryMotorSubsystem subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public TurnMotorOffWithRequires(BinaryMotorSubsystem subsystem) {
		this.subsystem = subsystem;
		requires((Subsystem) subsystem);
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("TurnMotorOffWithRequires init.", this.getClass());
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
		Logger.addEvent("TurnMotorOffWithRequires end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("TurnMotorOffWithRequires Interrupted!", this.getClass());
	}
}