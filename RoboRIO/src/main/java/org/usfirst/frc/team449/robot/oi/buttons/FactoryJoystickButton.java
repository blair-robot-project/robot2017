package org.usfirst.frc.team449.robot.oi.buttons;

import edu.wpi.first.wpilibj.Joystick;

/**
 * A version of {@link edu.wpi.first.wpilibj.buttons.JoystickButton} that is a FactoryButton.
 */
public class FactoryJoystickButton extends FactoryButton{

	/**
	 * The joystick the button is on.
	 */
	private Joystick joystick;

	/**
	 * The port of the button on the joystick.
	 */
	private int buttonNum;

	/**
	 * Default constructor.
	 * @param joystick The joystick the button is on.
	 * @param buttonNum The port of the button. Note that button numbers begin at 1, not 0.
	 */
	FactoryJoystickButton(Joystick joystick, int buttonNum) {
		this.joystick = joystick;
		this.buttonNum = buttonNum;
	}

	/**
	 * Get whether the button is pressed.
	 * @return true if the button is pressed, false otherwise.
	 */
	@Override
	public boolean get() {
		return joystick.getRawButton(buttonNum);
	}
}
