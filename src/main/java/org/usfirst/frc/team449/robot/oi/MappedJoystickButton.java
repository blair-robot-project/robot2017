package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import maps.org.usfirst.frc.team449.robot.oi.JoystickButtonMap;

/**
 * A button with a port and joystick that can be constructed from a map object.
 */
public class MappedJoystickButton extends JoystickButton {

	/**
	 * Construct a MappedJoystickButton
	 * @param map config map
	 */
	public MappedJoystickButton(JoystickButtonMap.JoystickButton map) {
		super(new Joystick(map.getJoystickIndex()), map.getButtonIndex());
	}
}
