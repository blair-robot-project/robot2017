package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.filters.LinearDigitalFilter;

/**
 * A smoothed throttle with a deadband.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_OBJECT, property="@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedSmoothedThrottle extends MappedThrottle {

	protected final double deadband;

	private final LinearDigitalFilter filter;

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
	                              double scalingTimeConstantSecs,
	                              double deadband,
	                              boolean inverted) {
		super(stick, axis, inverted);
		this.deadband = deadband;
		filter = LinearDigitalFilter.singlePoleIIR(this, scalingTimeConstantSecs, 20./1000.);
	}

	/**
	 * Gets the value from the joystick and smoothes it.
	 *
	 * @return The joystick's value, after processed with a smoothing function.
	 */
	@Override
	public double getValue() {
		//Get the smoothed value
		double input = filter.get();

		double sign = Math.signum(input);
		input = Math.abs(input);

		//apply the deadband.
		if (input < deadband) {
			return 0;
		}

		//scale so f(deadband) is 0 and f(1) is 1.
		input = (input-deadband)/(1.-deadband);

		return sign*input;
	}
}