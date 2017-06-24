package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * A polynomically scaled throttle.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedPolyThrottle extends MappedSmoothedThrottle {
	/**
	 * The power that X is raised to.
	 */
	protected Map<Double, Double> powerToCoefficientMap;

	/**
	 * A basic constructor.
	 *
	 * @param stick    The Joystick object being used
	 * @param axis     The axis being used. 0 is X, 1 is Y, 2 is Z.
	 * @param deadband The deadband below which the input will be read as 0, on [0, 1]. Defaults to 0.
	 * @param inverted Whether or not to invert the joystick input. Defaults to false.
	 */
	@JsonCreator
	public MappedPolyThrottle(@JsonProperty(required = true) MappedJoystick stick,
	                          @JsonProperty(required = true) int axis,
	                          double scalingTimeConstantSecs,
	                          double deadband,
	                          boolean inverted,
	                          @JsonProperty(required = true) Map<Double, Double> powerToCoefficientMap) {
		super(stick, axis, scalingTimeConstantSecs, deadband, inverted);
		if (powerToCoefficientMap.size() == 0) {
			powerToCoefficientMap.put(1., 1.);
		}
		double sum = 0;
		for (Double power : powerToCoefficientMap.keySet()){
			if (power < 0){
				throw new IllegalArgumentException("Negative exponents are not allowed!");
			}
			sum += powerToCoefficientMap.get(power);
		}
		//Round the sum to avoid floating-point errors
		BigDecimal bd = new BigDecimal(sum);
		bd = bd.setScale(3, RoundingMode.HALF_UP);
		if (bd.doubleValue() != 1){
			throw new IllegalArgumentException("Polynomial coefficients don't add up to 1!");
		}
		this.powerToCoefficientMap = powerToCoefficientMap;
	}

	/**
	 * Raises the value of the smoothed joystick output to the degreeth power, while preserving sign.
	 *
	 * @return The processed value of the joystick
	 */
	@Override
	public double getValue() {
		double input = super.getValue();
		double sign = Math.signum(input);
		input = Math.abs(input);

		double toRet = 0;
		for (Double power : powerToCoefficientMap.keySet()){
			toRet += Math.pow(input, power)*powerToCoefficientMap.get(power);
		}
		return toRet*sign;
	}
}