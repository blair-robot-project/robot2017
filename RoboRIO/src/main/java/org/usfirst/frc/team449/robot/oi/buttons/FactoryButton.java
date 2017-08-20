package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedJoystick;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;

/**
 * A factory for constructing a button..
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public abstract class FactoryButton extends Button {

	/**
	 * Static factory for constructing different types of buttons.
	 *
	 * @param joystick     The joystick that the button is on.
	 * @param buttonNumber The index of the button on the joystick (starts at 1). Must be null if either triggerAxis or
	 *                     angle isn't, but otherwise must have a value. Indicates that this is a simple {@link
	 *                     JoystickButton}.
	 * @param triggerAxis  The stick axis for the trigger that this button triggers based off of. Must be null if either
	 *                     buttonNumber or angle isn't, but otherwise must have a value. Indicates that this is a {@link
	 *                     TriggerButton}.
	 * @param triggerAt    The amount, on [0, 1], that the joystick must be pushed to trigger. Must be null if
	 *                     triggerAxis is, and otherwise must have a value.
	 * @param angle        The angle the D-pad needs to be pushed to to trigger this button. Must be null if either
	 *                     triggerAxis or buttonNumber isn't, but otherwise must have a value. Indicates that this is a
	 *                     {@link dPadButton}.
	 * @return A Button constructed from the given parameters.
	 */
	@NotNull
	@JsonCreator
	public static FactoryButton constructButton(@NotNull @JsonProperty(required = true) MappedJoystick joystick,
	                                            @Nullable Integer buttonNumber,
	                                            @Nullable Integer triggerAxis,
	                                            @Nullable Double triggerAt,
	                                            @Nullable Integer angle) {
		if (triggerAxis != null) {
			return new TriggerButton(new Throttle(joystick, triggerAxis, false), triggerAt);
		} else if (angle != null) {
			return new dPadButton(joystick, angle);
		} else {
			return new FactoryJoystickButton(joystick, buttonNumber);
		}
	}
}
