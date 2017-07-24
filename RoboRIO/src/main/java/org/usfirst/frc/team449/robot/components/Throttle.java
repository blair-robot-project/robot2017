package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A class representing a single axis on a joystick.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Throttle implements PIDSource {

	/**
	 * The stick we're using
	 */
	@NotNull
	protected final Joystick stick;

	/**
	 * The axis on the joystick we care about.
	 */
	private final int axis;

	/**
	 * Whether or not the controls should be inverted
	 */
	private final boolean inverted;

	/**
	 * A basic constructor.
	 *
	 * @param stick    The Joystick object being used
	 * @param axis     The axis being used. 0 is X, 1 is Y, 2 is Z.
	 * @param inverted Whether or not to invert the joystick input. Defaults to false.
	 */
	@JsonCreator
	public Throttle(@NotNull @JsonProperty(required = true) MappedJoystick stick,
	                @JsonProperty(required = true) int axis,
	                boolean inverted) {
		this.stick = stick;
		this.axis = axis;
		this.inverted = inverted;
	}

	/**
	 * Gets the raw value from the stick and inverts it if necessary. This is private so it's not overriden, allowing it
	 * to be used by both getValue and pidGet without causing a circular reference.
	 *
	 * @return The raw joystick output, on [-1, 1].
	 */
	private double getValuePrivate() {
		return (inverted ? -1 : 1) * stick.getRawAxis(axis);
	}

	/**
	 * Gets the raw value from the stick and inverts it if necessary.
	 *
	 * @return The raw joystick output, on [-1, 1].
	 */
	public double getValue() {
		return getValuePrivate();
	}

	/**
	 * Get which parameter of the device you are using as a process control variable. We don't use this.
	 *
	 * @return the currently selected PID source parameter
	 */
	@Override
	@Nullable
	public PIDSourceType getPIDSourceType() {
		return null;
	}

	/**
	 * Set which parameter of the device you are using as a process control variable. We don't use this.
	 *
	 * @param pidSource An enum to select the parameter.
	 */
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		//Do nothing!
	}

	/**
	 * Get the result to use in PIDController.
	 *
	 * @return the result to use in PIDController
	 */
	@Override
	public double pidGet() {
		return getValuePrivate();
	}
}