package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;

/**
 * A wrapper command that switches to low gear.
 */
public class SwitchToLowGear extends Command {
	
	private ShiftingDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	public SwitchToLowGear(ShiftingDrive subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("SwitchToLowGear init.");
	}

	/**
	 * Switch to low gear
	 */
	@Override
	protected void execute() {
		subsystem.setGear(ShiftingDrive.gear.LOW);
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
		System.out.println("SwitchToLowGear end.");
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("SwitchToLowGear Interrupted!");
	}
}