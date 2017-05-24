package org.usfirst.frc.team449.robot.oi.buttons;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import maps.org.usfirst.frc.team449.robot.oi.JoystickButtonMap;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * A button that gets triggered by a specific throttle being held down at or over a certain amount.
 */
public class TriggerButton extends Button {

	/**
	 * The relevant throttle.
	 */
	private Throttle throttle;

	/**
	 * The percentage pressed to trigger at, from (0, 1]
	 */
	private double triggerAt;

	/**
	 * Argument-based constructor.
	 * @param port The port of the joystick containing the throttle.
	 * @param axis The axis of the throttle.
	 * @param triggerAt The percentage pressed to trigger at, from (0, 1]
	 */
	public TriggerButton(int port, int axis, double triggerAt) {
		throttle = new SmoothedThrottle(new Joystick(port), axis);
		this.triggerAt = triggerAt;
	}

	/**
	 * Map-based constructor
	 * @param map config map
	 */
	public TriggerButton(JoystickButtonMap.JoystickButton map) {
		this(map.getPort(), map.getButtonIndex(), map.getTriggerAt());
	}

	/**
	 * Get whether this button is pressed.
	 * @return true if the throttle's output is greater than or equal to the trigger threshold, false otherwise.
	 */
	@Override
	public boolean get() {
		return Math.abs(throttle.getValue()) >= triggerAt;
	}
}
