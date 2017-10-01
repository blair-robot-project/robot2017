package org.usfirst.frc.team449.robot.oi.fieldoriented;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.Nullable;

/**
 * An OI that gives an absolute heading, relative to the field, and a velocity.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface OIFieldOriented {

	/**
	 * Get the absolute angle for the robot to move towards.
	 *
	 * @return An angular setpoint for the robot in degrees, where 0 is pointing at the other alliance's driver station
	 * and 90 is pointing at the left wall when looking out from the driver station. Returns null if vel is 0.
	 */
	@Nullable
	Double getTheta();

	/**
	 * Get the velocity for the robot to go at.
	 *
	 * @return A velocity from [-1, 1].
	 */
	double getVel();

}
