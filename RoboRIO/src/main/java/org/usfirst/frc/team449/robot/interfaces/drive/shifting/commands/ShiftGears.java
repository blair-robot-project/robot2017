package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Shifts gears. Basically a "ToggleGear" command.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class ShiftGears extends Command {

	/**
	 * The drive to execute this command on
	 */
	private ShiftingDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The drive to execute this command on
	 */
	public ShiftGears(ShiftingDrive subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("ShiftGears init.", this.getClass());
	}

	/**
	 * Switch gears
	 */
	@Override
	protected void execute() {
		subsystem.setGear(subsystem.getGear() == ShiftingDrive.gear.LOW ? ShiftingDrive.gear.HIGH : ShiftingDrive.gear.LOW);
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
		Logger.addEvent("ShiftGears end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("ShiftGears Interrupted!", this.getClass());
	}
}