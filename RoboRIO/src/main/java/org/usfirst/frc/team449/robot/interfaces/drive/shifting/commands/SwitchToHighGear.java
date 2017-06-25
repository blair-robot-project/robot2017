package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlCommandWrapper;

/**
 * A command that switches to high gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SwitchToHighGear extends YamlCommandWrapper {

	/**
	 * The drive subsystem to execute this command on.
	 */
	@NotNull
	private final ShiftingDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	@JsonCreator
	public SwitchToHighGear(@NotNull @JsonProperty(required = true) ShiftingDrive subsystem) {
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