package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

/**
 * A class representing a single axis on a joystick.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_OBJECT, property="@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedThrottle implements PIDSource{
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

	/**
	 * Set which parameter of the device you are using as a process control variable. We don't use this.
	 *
	 * @param pidSource An enum to select the parameter.
	 */
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {}

	/**
	 * Get which parameter of the device you are using as a process control variable. We don't use this.
	 *
	 * @return the currently selected PID source parameter
	 */
	@Override
	public PIDSourceType getPIDSourceType() {
		return null;
	}

	/**
	 * Get the result to use in PIDController.
	 *
	 * @return the result to use in PIDController
	 */
	@Override
	public double pidGet() {
		return (inverted ? -1 : 1) * stick.getRawAxis(axis);
	}
}