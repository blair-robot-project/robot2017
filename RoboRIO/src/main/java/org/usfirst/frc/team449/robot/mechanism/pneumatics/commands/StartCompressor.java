package org.usfirst.frc.team449.robot.mechanism.pneumatics.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlCommandWrapper;

/**
 * Start up the pneumatic compressor.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class StartCompressor extends YamlCommandWrapper {

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final PneumaticsSubsystem subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	@JsonCreator
	public StartCompressor(@NotNull @JsonProperty(required = true) PneumaticsSubsystem subsystem) {
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("StartCompressor init.", this.getClass());
	}

	/**
	 * Start the compressor.
	 */
	@Override
	protected void execute() {
		subsystem.startCompressor();
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
		Logger.addEvent("StartCompressor end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("StartCompressor Interrupted!", this.getClass());
	}
}