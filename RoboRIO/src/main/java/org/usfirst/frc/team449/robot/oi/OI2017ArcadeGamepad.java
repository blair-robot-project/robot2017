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
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class OI2017ArcadeGamepad extends ArcadeOI {

	/**
	 * How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the turning stick that
	 * much of the way
	 */
	private double shift;

	/**
	 * The throttle wrapper for the stick controlling turning velocity
	 */
	private MappedSmoothedThrottle rotThrottle;

	/**
	 * The throttle wrapper for the stick controlling linear velocity
	 */
	private MappedSmoothedThrottle fwdThrottle;

	/**
	 * The controller with the drive sticks
	 */
	private Joystick gamepad;

	/**
	 * The button-command mappings for running commands. This only exists to prevent garbage collection.
	 */
	private List<CommandButton> buttons;

	/**
	 * Scaling, from [0, 1], that the rotational throttle decreases the forwards throttle by. Used so that turning while
	 * at high speed still has an impact.
	 */
	private double rotScale;

	/**
	 * Default constructor
	 * @param gamepad The gamepad containing the joysticks and buttons.
	 * @param rotThrottle The throttle for rotating the robot.
	 * @param fwdThrottle The throttle for driving the robot straight.
	 * @param invertDPad Whether or not to invert the D-pad.
	 * @param dPadShift How fast the dPad should turn the robot, on [0, 1].
	 * @param scaleFwdByRotCoefficient Scaling, from [0, 1], that the rotational throttle decreases the forwards
	 *                                    throttle by. Used so that turning while at high speed still has an impact.
	 *                                    Defaults to 0.
	 * @param buttons The button-command mappings for running commands.
	 */
	@JsonCreator
	public OI2017ArcadeGamepad(@JsonProperty(required = true) MappedJoystick gamepad,
	                           @JsonProperty(required = true) MappedSmoothedThrottle rotThrottle,
	                           @JsonProperty(required = true) MappedSmoothedThrottle fwdThrottle,
	                           @JsonProperty(required = true) boolean invertDPad,
	                           @JsonProperty(required = true) double dPadShift,
	                           double scaleFwdByRotCoefficient,
	                           List<CommandButton> buttons) {
		//Instantiate stick and joysticks
		this.gamepad = gamepad;
		this.rotThrottle = rotThrottle;
		this.fwdThrottle = fwdThrottle;
		this.buttons = buttons;

		//Set up other map constants
		shift = invertDPad ? -dPadShift : dPadShift;
		rotScale = scaleFwdByRotCoefficient;

	}

	/**
	 * The output of the throttle controlling linear velocity, smoothed and adjusted according to what type of
	 * joystick it is.
	 *
	 * @return The processed stick output, sign-adjusted so 1 is forward and -1 is backwards.
	 */
	public double getFwd() {
		//Scale based on rotational throttle for more responsive turning at high speed
		return fwdThrottle.getValue() * (1 - rotScale * rotThrottle.getValue());
	}

	/**
	 * Get the output of the D-pad or turning joystick, whichever is in use. If both are in use, the D-pad takes
	 * preference.
	 *
	 * @return The processed stick or D-pad output, sign-adjusted so 1 is right and -1 is left.
	 */
	public double getRot() {
		//If the gamepad is being pushed to the left or right
		if (!(gamepad.getPOV() == -1 || gamepad.getPOV() % 180 == 0)) {
			//Output the shift value
			return gamepad.getPOV() < 180 ? shift : -shift;
		} else {
			//Return the throttle value if it's outside of the deadband.
			return rotThrottle.getValue();
		}
	}
}
