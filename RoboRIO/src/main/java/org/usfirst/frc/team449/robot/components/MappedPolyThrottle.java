package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.util.Polynomial;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * A polynomially scaled throttle.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedPolyThrottle extends MappedSmoothedThrottle {
	/**
	 * The power that X is raised to.
	 */
	@NotNull
	protected final Polynomial polynomial;

	private double input;

	private double sign;

	/**
	 * A basic constructor.
	 *
	 * @param stick    The Joystick object being used
	 * @param axis     The axis being used. 0 is X, 1 is Y, 2 is Z.
	 * @param deadband The deadband below which the input will be read as 0, on [0, 1]. Defaults to 0.
	 * @param inverted Whether or not to invert the joystick input. Defaults to false.
	 */
	@JsonCreator
	public MappedPolyThrottle(@NotNull @JsonProperty(required = true) MappedJoystick stick,
	                          @JsonProperty(required = true) int axis,
	                          double smoothingTimeConstantSecs,
	                          double deadband,
	                          boolean inverted,
	                          @NotNull @JsonProperty(required = true) Polynomial polynomial) {
		super(stick, axis, smoothingTimeConstantSecs, deadband, inverted);
		double sum = 0;
		for (Double power : polynomial.getPowerToCoefficientMap().keySet()) {
			if (power < 0) {
				throw new IllegalArgumentException("Negative exponents are not allowed!");
			}
			sum += polynomial.getPowerToCoefficientMap().get(power);
		}
		//Round the sum to avoid floating-point errors
		BigDecimal bd = new BigDecimal(sum);
		bd = bd.setScale(3, RoundingMode.HALF_UP);
		if (bd.doubleValue() != 1) {
			throw new IllegalArgumentException("Polynomial coefficients don't add up to 1!");
		}
		this.polynomial = polynomial;
	}

	/**
	 * Raises the value of the smoothed joystick output to the degreeth power, while preserving sign.
	 *
	 * @return The processed value of the joystick
	 */
	@Override
	public double getValue() {
		return polynomial.get(super.getValue());
	}
}