package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.DriveShifting;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlCommandWrapper;

/**
 * Shifts gears. Basically a "ToggleGear" command.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShiftGears extends YamlCommandWrapper {

	/**
	 * The drive to execute this command on
	 */
	@NotNull
	private final DriveShifting subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The drive to execute this command on
	 */
	@JsonCreator
	public ShiftGears(@NotNull @JsonProperty(required = true) DriveShifting subsystem) {
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
		subsystem.setGear(subsystem.getGear() == DriveShifting.gear.LOW ? DriveShifting.gear.HIGH : DriveShifting.gear.LOW);
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