package org.usfirst.frc.team449.robot.interfaces.subsystem.conditional;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * A subsystem with a condition that's sometimes met, e.g. a limit switch, a current/power limit, an IR sensor.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemConditional {

	/**
	 * @return true if the condition is met, false otherwise
	 */
	boolean isConditionTrue();
}
