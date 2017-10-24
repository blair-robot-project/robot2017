package org.usfirst.frc.team449.robot.subsystem.interfaces.navX.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlCommandWrapper;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;

/**
 * Set the heading of the navX.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetNavxHeading extends YamlCommandWrapper {

	/**
	 * Whether or not to override the navX.
	 */
	private final double heading;

	/**
	 * The subsystem to execute this command on.
	 */
	@NotNull
	private final SubsystemNavX subsystem;

	/**
	 * Default constructor.
	 *
	 * @param subsystem The subsystem to execute this command on
	 * @param heading The angle, in degrees from [-180, 180], to set the NavX's heading to.
	 */
	@JsonCreator
	public SetNavxHeading(@NotNull @JsonProperty(required = true) SubsystemNavX subsystem,
	                      @JsonProperty(required = true) double heading) {
		this.heading = heading;
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("SetNavxHeading init", this.getClass());
	}

	/**
	 * Set whether or not we're overriding the navX
	 */
	@Override
	protected void execute() {
		subsystem.setHeading(heading);
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
		Logger.addEvent("SetNavxHeading end", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("SetNavxHeading Interrupted!", this.getClass());
	}
}

