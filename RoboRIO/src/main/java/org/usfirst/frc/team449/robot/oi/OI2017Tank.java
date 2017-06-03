package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.components.MappedSmoothedThrottle;
import org.usfirst.frc.team449.robot.interfaces.oi.TankOI;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;

import java.util.List;

/**
 * A simple tank drive, where each joystick controls a side of the robot.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OI2017Tank extends TankOI {
	/**
	 * The left throttle
	 */
	private MappedSmoothedThrottle leftThrottle;

	/**
	 * The right throttle
	 */
	private MappedSmoothedThrottle rightThrottle;

	/**
	 * The button-command mappings for running commands. This only exists to prevent garbage collection.
	 */
	private List<CommandButton> buttons;

	/**
	 * Default constructor
	 *
	 * @param leftThrottle  The throttle for controlling the velocity of the left side of the drive.
	 * @param rightThrottle The throttle for controlling the velocity of the right side of the drive.
	 * @param buttons       The button-command mappings for running commands.
	 */
	@JsonCreator
	public OI2017Tank(@JsonProperty(required = true) MappedSmoothedThrottle leftThrottle,
	                  @JsonProperty(required = true) MappedSmoothedThrottle rightThrottle,
	                  List<CommandButton> buttons) {
		this.leftThrottle = leftThrottle;
		this.rightThrottle = rightThrottle;
		this.buttons = buttons;
	}

	/**
	 * @return throttle to the left motor cluster [-1, 1]
	 */
	@Override
	public double getLeftThrottle() {
		return leftThrottle.getValue();
	}

	/**
	 * @return throttle to the right motor cluster [-1, 1]
	 */
	@Override
	public double getRightThrottle() {
		return rightThrottle.getValue();
	}
}