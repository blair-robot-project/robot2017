package org.usfirst.frc.team449.robot.oi.buttons;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import maps.org.usfirst.frc.team449.robot.oi.JoystickButtonMap;

/**
 * A Button triggered by pushing the D-pad to a specific angle.
 */
public class dPadButton extends Button {

	/**
	 * The angle that the D-pad must be pushed to to trigger this button.
	 */
	private int angle;

	/**
	 * The joystick with the relevant D-pad on it.
	 */
	private Joystick joystick;

	/**
	 * Explicit argument constructor.
	 * @param port The port the relevant joystick is plugged into.
	 * @param angle The angle that the D-pad must be pushed to to trigger this button.
	 */
	public dPadButton(int port, int angle) {
		this.angle = angle;
		this.joystick = new Joystick(port);
	}

	/**
	 * Map-based constructor
	 * @param map Map containing constants defining this object.
	 */
	public dPadButton(JoystickButtonMap.JoystickButton map) {
		this(map.getPort(), map.getAngle());
	}

	/**
	 * Get whether this button is pressed
	 * @return true if the joystick's D-pad is pressed to the given angle, false otherwise.
	 */
	@Override
	public boolean get() {
		return joystick.getPOV() == angle;
	}
}
