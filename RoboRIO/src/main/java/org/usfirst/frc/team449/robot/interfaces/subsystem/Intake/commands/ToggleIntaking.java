package org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A command that does an instantaneous change (extend a piston, turn on a motor, etc.)
 */
public class ToggleIntaking extends Command {

	private IntakeSubsystem subsystem;

	private IntakeSubsystem.IntakeMode speed;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	public ToggleIntaking(IntakeSubsystem subsystem, IntakeSubsystem.IntakeMode speed) {
		this.subsystem = subsystem;
		this.speed = speed;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("SetIntakeMode init.", this.getClass());
	}

	/**
	 * Do the state change.
	 */
	@Override
	protected void execute() {
		if (subsystem.getMode() == IntakeSubsystem.IntakeMode.OFF) {
			subsystem.setMode(speed);
		} else {
			subsystem.setMode(IntakeSubsystem.IntakeMode.OFF);
		}
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
		Logger.addEvent("SetIntakeMode end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("SetIntakeMode Interrupted!", this.getClass());
	}
}