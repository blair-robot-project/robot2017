package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;

/**
 * A wrapper command that switches to high gear.
 */
public class SwitchToGear extends Command {

	private ShiftingDrive subsystem;
	private ShiftingDrive.gear switchTo;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	public SwitchToGear(ShiftingDrive subsystem, ShiftingDrive.gear switchTo) {
		this.subsystem = subsystem;
		this.switchTo = switchTo;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("SwitchToGear init.");
	}

	/**
	 * Switch to high gear
	 */
	@Override
	protected void execute() {
		subsystem.setGear(switchTo);
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
		System.out.println("SwitchToGear end.");
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("SwitchToGear Interrupted!");
	}
}