package org.usfirst.frc.team449.robot.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.Robot;

/**
 * A command that does nothing and finishes after a set number of milliseconds. For use to create a delay in sequential
 * CommandGroups.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class WaitForMillis extends YamlCommandWrapper {

	/**
	 * How long this command takes to finish, in milliseconds.
	 */
	private final long timeout;

	/**
	 * The time this command started at.
	 */
	private long startTime;

	/**
	 * Default constructor
	 *
	 * @param time How long this command will take to finish, in milliseconds.
	 */
	@JsonCreator
	public WaitForMillis(@JsonProperty(required = true) long time) {
		timeout = time;
	}

	/**
	 * Store the start time.
	 */
	@Override
	protected void initialize() {
		startTime = Robot.currentTimeMillis();
	}

	/**
	 * The execute method is called repeatedly until this Command either finishes
	 * or is canceled.
	 */
	@Override
	protected void execute() {

	}

	/**
	 * Finish if the specified amount of time has passed.
	 *
	 * @return true if the specified number of milliseconds have passed since this command started, false otherwise.
	 */
	@Override
	protected boolean isFinished() {
		return Robot.currentTimeMillis() - startTime >= timeout;
	}

	/**
	 * Do nothing.
	 */
	@Override
	protected void end() {

	}

	/**
	 * Do nothing.
	 */
	@Override
	protected void interrupted() {

	}
}
