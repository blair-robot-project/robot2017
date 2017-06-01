package org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Toggles whether the subsystem is off or set to a given mode.
 */
public class ToggleIntaking extends Command {

	/**
	 * The subsystem to execute this command on.
	 */
	private IntakeSubsystem subsystem;

	/**
	 * The mode to set this subsystem to if it's currently off.
	 */
	private IntakeSubsystem.IntakeMode mode;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 * @param mode      The mode to set this subsystem to if it's currently off.
	 */
	public ToggleIntaking(IntakeSubsystem subsystem, IntakeSubsystem.IntakeMode mode) {
		this.subsystem = subsystem;
		this.mode = mode;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("SetIntakeMode init.", this.getClass());
	}

	/**
	 * Set the subsystem to the specified mode if it's off, and set it to off otherwise.
	 */
	@Override
	protected void execute() {
		if (subsystem.getMode() == IntakeSubsystem.IntakeMode.OFF) {
			subsystem.setMode(mode);
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