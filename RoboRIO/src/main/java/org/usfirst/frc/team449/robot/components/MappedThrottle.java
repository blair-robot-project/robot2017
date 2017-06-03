package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;

/**
 * A class representing a single axis on a joystick.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedThrottle {
	//The stick we're using
	protected Joystick stick;

	//The axis on the joystick we care about. Usually 1.
	protected int axis;

	//Whether or not the controls should be inverted
	protected boolean inverted;

	/**
	 * A basic constructor.
	 *
	 * @param stick    The Joystick object being used
	 * @param axis     The axis being used. 0 is X, 1 is Y, 2 is Z.
	 * @param inverted Whether or not to invert the joystick input. Defaults to false.
	 */
	@JsonCreator
	public MappedThrottle(@JsonProperty(required = true) MappedJoystick stick,
	                      @JsonProperty(required = true) int axis,
	                      boolean inverted) {
		this.stick = stick;
		this.axis = axis;
		this.inverted = inverted;
	}

	/**
	 * Gets the raw value from the stick and inverts it if necessary.
	 *
	 * @return The raw joystick output.
	 */
	public double getValue() {
		return (inverted ? -1 : 1) * stick.getRawAxis(axis);
	}
}