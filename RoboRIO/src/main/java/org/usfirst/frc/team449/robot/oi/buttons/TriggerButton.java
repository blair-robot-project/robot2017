package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MappedJoystick;
import org.usfirst.frc.team449.robot.components.MappedSmoothedThrottle;
import org.usfirst.frc.team449.robot.components.MappedThrottle;

/**
 * A button that gets triggered by a specific throttle being held down at or over a certain amount.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TriggerButton extends FactoryButton {

	/**
	 * The relevant throttle.
	 */
	@NotNull
	private final MappedThrottle throttle;

	/**
	 * The percentage pressed to trigger at, from (0, 1]
	 */
	private final double triggerAt;

	/**
	 * Argument-based constructor.
	 *
	 * @param joystick  The the joystick containing the throttle.
	 * @param axis      The axis of the throttle.
	 * @param triggerAt The percentage pressed to trigger at, from (0, 1]
	 */
	public TriggerButton(@NotNull @JsonProperty(required = true) MappedJoystick joystick,
	                     @JsonProperty(required = true) int axis,
	                     @JsonProperty(required = true) double triggerAt) {
		throttle = new MappedSmoothedThrottle(joystick, axis, 0, 0, false);
		this.triggerAt = triggerAt;
	}

	/**
	 * Get whether this button is pressed.
	 *
	 * @return true if the throttle's output is greater than or equal to the trigger threshold, false otherwise.
	 */
	@Override
	public boolean get() {
		return Math.abs(throttle.getValue()) >= triggerAt;
	}
}
