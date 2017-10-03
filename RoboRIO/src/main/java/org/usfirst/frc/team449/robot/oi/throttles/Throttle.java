package org.usfirst.frc.team449.robot.oi.throttles;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * An object representing an axis of a stick on a joystick.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface Throttle {

	/**
	 * Get the output of the throttle this object represents.
	 *
	 * @return The output from [-1, 1].
	 */
	double getValue();
}
