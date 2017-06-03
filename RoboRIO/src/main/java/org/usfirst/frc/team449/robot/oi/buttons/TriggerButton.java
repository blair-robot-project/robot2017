package org.usfirst.frc.team449.robot.oi.buttons;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * A button that gets triggered by a specific throttle being held down at or over a certain amount.
 */
public class TriggerButton extends FactoryButton {

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
	 *
	 * @param joystick      The the joystick containing the throttle.
	 * @param axis      The axis of the throttle.
	 * @param triggerAt The percentage pressed to trigger at, from (0, 1]
	 */
	TriggerButton(Joystick joystick, int axis, double triggerAt) {
		throttle = new SmoothedThrottle(joystick, axis);
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
