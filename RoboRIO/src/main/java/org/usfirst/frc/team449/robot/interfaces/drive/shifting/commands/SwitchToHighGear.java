package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * A wrapper command that switches to high gear.
 */
public class SwitchToHighGear extends Command {

	private ShiftingDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	public SwitchToHighGear(ShiftingDrive subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("SwitchToHighGear init.", this.getClass());
	}

	/**
	 * Switch to high gear
	 */
	@Override
	protected void execute() {
		subsystem.setGear(ShiftingDrive.gear.HIGH);
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
		Logger.addEvent("SwitchToHighGear end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("SwitchToHighGear Interrupted!", this.getClass());
	}
}