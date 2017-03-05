package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import maps.org.usfirst.frc.team449.robot.oi.JButtonMap;

/**
 * Created by ryant on 2017-02-18.
 */
public class MJButton extends JoystickButton {
	public MJButton(JButtonMap.JButton map) {
		super(new Joystick(map.getJi()), map.getBi());
	}
}
