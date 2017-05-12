package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import maps.org.usfirst.frc.team449.robot.oi.JoystickButtonMap;
import org.usfirst.frc.team449.robot.components.TriggerButton;
import org.usfirst.frc.team449.robot.components.dPadButton;

/**
 * A button with a port and joystick that can be constructed from a map object.
 */
public class MappedJoystickButton extends JoystickButton {

	/**
	 * Construct a MappedJoystickButton
	 * @param map config map
	 */
	public MappedJoystickButton(JoystickButtonMap.JoystickButton map) {
		super(new Joystick(map.getPort()), map.getButtonIndex());
	}

	public static Button constructButton(JoystickButtonMap.JoystickButton map){
		if (map.hasTriggerAt()){
			return new TriggerButton(map);
		} else if(map.hasAngle()) {
			return new dPadButton(map);
		} else {
			return new MappedJoystickButton(map);
		}
	}
}
