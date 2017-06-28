package org.usfirst.frc.team449.robot.interfaces.subsystem.conditional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A subsystem with a condition that's sometimes met, e.g. a limit switch, a current/power limit, an IR sensor.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface ConditionalSubsystem {

	/**
	 * Whether or not the condition had been met
	 *
	 * @return true if met, false otherwise
	 */
	boolean isConditionTrue();
}
