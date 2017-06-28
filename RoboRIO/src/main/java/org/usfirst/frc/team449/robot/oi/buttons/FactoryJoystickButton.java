package org.usfirst.frc.team449.robot.oi.buttons;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MappedJoystick;

import java.util.Map;

/**
 * A version of {@link edu.wpi.first.wpilibj.buttons.JoystickButton} that is a FactoryButton.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FactoryJoystickButton extends FactoryButton {

	/**
	 * The joystick the button is on.
	 */
	@NotNull
	private final Joystick joystick;

	/**
	 * The port of the button on the joystick.
	 */
	private final int buttonNum;

	/**
	 * Default constructor.
	 *
	 * @param joystick  The joystick the button is on.
	 * @param buttonNum The port of the button. Note that button numbers begin at 1, not 0.
	 */
	@JsonCreator
	public FactoryJoystickButton(@NotNull @JsonProperty(required = true) MappedJoystick joystick,
	                             @JsonProperty(required = true) int buttonNum) {
		this.joystick = joystick;
		this.buttonNum = buttonNum;
	}

	/**
	 * Get whether the button is pressed.
	 *
	 * @return true if the button is pressed, false otherwise.
	 */
	@Override
	public boolean get() {
		return joystick.getRawButton(buttonNum);
	}
}
