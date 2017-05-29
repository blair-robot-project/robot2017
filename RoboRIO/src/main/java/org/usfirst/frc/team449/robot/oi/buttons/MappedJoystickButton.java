package org.usfirst.frc.team449.robot.oi.buttons;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import maps.org.usfirst.frc.team449.robot.oi.JoystickButtonMap;

/**
 * A button with a port and joystick that can be constructed from a map object.
 */
public class MappedJoystickButton extends JoystickButton {

	/**
	 * Construct a MappedJoystickButton
	 *
	 * @param map config map
	 */
	public MappedJoystickButton(JoystickButtonMap.JoystickButton map) {
		super(new Joystick(map.getPort()), map.getButtonIndex());
	}

	/**
	 * Static factory method used to construct a Button from a map.
	 *
	 * @param map A map containing constants for a Trigger, dPad, or MappedJoystick button.
	 * @return A Button constructed from that map.
	 */
	public static Button constructButton(JoystickButtonMap.JoystickButton map) {
		if (map.hasTriggerAt()) {
			return new TriggerButton(map);
		} else if (map.hasAngle()) {
			return new dPadButton(map);
		} else {
			return new MappedJoystickButton(map);
		}
	}
}
