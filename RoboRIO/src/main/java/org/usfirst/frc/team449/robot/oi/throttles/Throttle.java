package org.usfirst.frc.team449.robot.oi.throttles;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.Joystick;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;

/**
 * A class representing a single axis on a joystick.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Throttle {

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
	 * Gets the raw value from the stick and inverts it if necessary.
	 *
	 * @return The raw joystick output, on [-1, 1].
	 */
	public double getValue() {
		return (inverted ? -1 : 1) * stick.getRawAxis(axis);
	}
}