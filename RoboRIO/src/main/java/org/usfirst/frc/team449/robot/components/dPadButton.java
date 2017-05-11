package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import maps.org.usfirst.frc.team449.robot.oi.JoystickButtonMap;

/**
 * Created by Noah Gleason on 4/6/2017.
 */
public class dPadButton extends Button {

	private int angle;
	private Joystick joystick;

	public dPadButton(int port, int angle) {
		this.angle = angle;
		this.joystick = new Joystick(port);
	}

	public dPadButton(JoystickButtonMap.JoystickButton map) {
		this(map.getPort(), map.getAngle());
	}

	@Override
	public boolean get() {
		return joystick.getPOV() == angle;
	}
}
