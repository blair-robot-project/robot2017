package org.usfirst.frc.team449.robot.util;

import org.usfirst.frc.team449.robot.Robot;

/**
 * An logged event with a message, timestamp, and calling class.
 */
public class LogEvent {
	/**
	 * The time, in milliseconds, at which this event was created.
	 */
	private long timeCalled;

	/**
	 * The message of this event.
	 */
	private String message;

	/**
	 * The class that called this event.
	 */
	private Class caller;

	/**
	 * Default constructor.
	 *
	 * Note to future people: Don't rewrite this to get the calling class from the stack trace. It's possible, and makes
	 * the code cleaner than taking the calling class as an argument, but getting the stack trace actually takes Java a
	 * little while, and considering how often this constructor is called, that would significantly slow us down.
	 *
	 * @param message The message of this event.
	 * @param caller The calling class. Should pretty much always be this.getClass().
	 */
	public LogEvent(String message, Class caller) {
		timeCalled = Robot.currentTimeMillis();
		this.message = message;
		this.caller = caller;
	}

	/**
	 * Turn this event into a string for logging.
	 * @return The time called, calling class, and message, comma-separated and in that order.
	 */
	public String toString() {
		return timeCalled +","+ caller.toString() +"," + message;
	}
}
