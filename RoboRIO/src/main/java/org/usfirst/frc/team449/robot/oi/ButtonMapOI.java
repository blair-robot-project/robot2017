package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team449.robot.components.MappedJoystick;
import org.usfirst.frc.team449.robot.components.MappedSmoothedThrottle;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;

import java.util.List;

/**
 * An OI for using an Xbox-style controller for an arcade drive, where one stick controls forward velocity and the other
 * controls turning velocity.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ButtonMapOI{

	/**
	 * The button-command mappings for running commands. This only exists to prevent garbage collection.
	 */
	private List<CommandButton> buttons;

	/**
	 * Default constructor
	 *
	 * @param buttons The button-command mappings for running commands.
	 */
	@JsonCreator
	public ButtonMapOI(List<CommandButton> buttons) {
		//Instantiate buttons
		this.buttons = buttons;
	}
}
