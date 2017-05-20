package org.usfirst.frc.team449.robot.util;

import org.usfirst.frc.team449.robot.Robot;

/**
 * Created by noah on 5/14/17.
 */
public class LogEvent {
	private long timeCalled;
	private String message;
	private Class caller;

	public LogEvent(String message, Class caller) {
		timeCalled = Robot.currentTimeMillis();
		this.message = message;
		this.caller = caller;
	}

	public long getTimeCalled() {
		return timeCalled;
	}

	public String getMessage() {
		return message;
	}

	public Class getCaller() {
		return caller;
	}

	public String toString() {
		return timeCalled + caller.toString() + message;
	}
}
