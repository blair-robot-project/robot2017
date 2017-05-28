package org.usfirst.frc.team449.robot.util;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.Robot;

/**
 * A command that does nothing and finishes after a set number of milliseconds. For use to create a delay in sequential CommandGroups.
 */
public class WaitForMillis extends Command {

	/**
	 * How long this command takes to finish, in milliseconds.
	 */
	private long timeout;

	/**
	 * The time this command started at.
	 */
	private long startTime;

	/**
	 * Default constructor
	 * @param time How long this command will take to finish, in milliseconds.
	 */
	public WaitForMillis(long time) {
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
	 * Finish if the specified amount of time has passed.
	 * @return true if the specified number of milliseconds have passed since this command started, false otherwise.
	 */
	@Override
	protected boolean isFinished() {
		return Robot.currentTimeMillis() - startTime >= timeout;
	}
}
