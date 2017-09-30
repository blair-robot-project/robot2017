package org.usfirst.frc.team449.robot.drive.unidirectional.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.other.Logger;

/**
 * A command to run the robot at a range of voltages and record the velocity.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DetermineVelVsVoltage <T extends YamlSubsystem & DriveUnidirectional> extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final T subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on
	 */
	@JsonCreator
	public DetermineVelVsVoltage(@NotNull @JsonProperty(required = true) T subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Reset the encoder position.
	 */
	@Override
	protected void initialize() {
		subsystem.resetPosition();
	}

	/**
	 *
	 */
	@Override
	protected void execute() {
		if ((subsystem.getLeftPos() + subsystem.getRightPos())/2 < 0)
	}

	/**
	 * @return
	 */
	@Override
	protected boolean isFinished() {
		//This does NOT have to be true.
		return true;
	}

	/**
	 *
	 */
	@Override
	protected void end() {
		Logger.addEvent("DetermineVelVsVoltage end.", this.getClass());
	}

	/**
	 *
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("DetermineVelVsVoltage Interrupted!", this.getClass());
	}
}