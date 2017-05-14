package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;

/**
 * A wrapper command that switches to high gear.
 */
public class ShiftGears extends Command {

	private ShiftingDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	public ShiftGears(ShiftingDrive subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		System.out.println("ShiftGears init.");
	}

	/**
	 * Switch to high gear
	 */
	@Override
	protected void execute() {
		subsystem.setGear(subsystem.getGear() == ShiftingDrive.gear.LOW? ShiftingDrive.gear.HIGH : ShiftingDrive.gear.LOW);
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
		System.out.println("ShiftGears end.");
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("ShiftGears Interrupted!");
	}
}