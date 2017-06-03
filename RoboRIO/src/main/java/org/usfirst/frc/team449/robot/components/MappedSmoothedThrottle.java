package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * A smoothed throttle with a deadband.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class MappedSmoothedThrottle extends MappedThrottle {

	private double deadband;

	/**
	 * A basic constructor.
	 *
	 * @param stick    The Joystick object being used
	 * @param axis     The axis being used. 0 is X, 1 is Y, 2 is Z.
	 * @param deadband The deadband below which the input will be read as 0, on [0, 1]. Defaults to 0.
	 * @param inverted Whether or not to invert the joystick input. Defaults to false.
	 */
	@JsonCreator
	public MappedSmoothedThrottle(@JsonProperty(required = true) MappedJoystick stick,
	                              @JsonProperty(required = true) int axis,
	                              double deadband,
	                              boolean inverted) {
		super(stick, axis, inverted);
		this.deadband = deadband;
	}

	/**
	 * Gets the value from the joystick and smoothes it.
	 *
	 * @return The joystick's value, after processed with a smoothing function.
	 */
	@Override
	public double getValue() {
		double input = super.getValue();
		int sign = (input < 0) ? -1 : 1; // get the sign of the input
		input *= sign; // get the absolute value

		//apply the deadband.
		if (input < deadband){
			input = 0;
		}

		//do some smoothing math
		return sign * (1 / (1 - Math.pow(deadband, 2))) * (Math.pow(input, 2) - Math.pow(deadband, 2));
	}
}