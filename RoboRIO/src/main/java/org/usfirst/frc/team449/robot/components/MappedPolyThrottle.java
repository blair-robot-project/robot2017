package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * A polynomically scaled throttle.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedPolyThrottle extends MappedSmoothedThrottle {
	/**
	 * The power that X is raised to.
	 */
	protected int degree;

	/**
	 * A basic constructor.
	 *
	 * @param stick    The Joystick object being used
	 * @param axis     The axis being used. 0 is X, 1 is Y, 2 is Z.
	 * @param deadband The deadband below which the input will be read as 0, on [0, 1]. Defaults to 0.
	 * @param inverted Whether or not to invert the joystick input. Defaults to false.
	 * @param degree   The power that X is raised to. Defaults to 1.
	 */
	@JsonCreator
	public MappedPolyThrottle(@JsonProperty(required = true) MappedJoystick stick,
	                          @JsonProperty(required = true) int axis,
	                          double deadband,
	                          boolean inverted,
	                          Integer degree) {
		super(stick, axis, deadband, inverted);
		if (degree == null) {
			degree = 1;
		}
		this.degree = degree;
	}

	/**
	 * Raises the value of the smoothed joystick output to the degreeth power, while preserving sign.
	 *
	 * @return The processed value of the joystick
	 */
	@Override
	public double getValue() {
		double input = super.getValue();
		if (degree % 2 == 0 && input < 0) {
			return -1 * Math.pow(input, degree);
		}
		return Math.pow(input, degree);
	}
}