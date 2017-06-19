package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team449.robot.components.MappedThrottle;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;

/**
 * Created by noahg on 18-Jun-17.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ArcadeOIWithDPad extends ArcadeOI{

	/**
	 * How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the turning stick that
	 * much of the way
	 */
	private double dPadShift;

	/**
	 * The throttle wrapper for the stick controlling turning velocity
	 */
	private MappedThrottle rotThrottle;

	/**
	 * The throttle wrapper for the stick controlling linear velocity
	 */
	private MappedThrottle fwdThrottle;

	/**
	 * The controller with the D-pad
	 */
	private Joystick gamepad;

	/**
	 * Scaling, from [0, 1], that the rotational throttle decreases the forwards throttle by. Used so that turning while
	 * at high speed still has an impact.
	 */
	private double rotScale;

	/**
	 * Default constructor
	 *
	 * @param gamepad                  The gamepad containing the joysticks and buttons. Can be null if not using the D-pad.
	 * @param rotThrottle              The throttle for rotating the robot.
	 * @param fwdThrottle              The throttle for driving the robot straight.
	 * @param invertDPad               Whether or not to invert the D-pad. Defaults to false.
	 * @param dPadShift                How fast the dPad should turn the robot, on [0, 1]. Defaults to 0.
	 * @param rotScale Scaling, from [0, 1], that the rotational throttle decreases the forwards
	 *                                 throttle by. Used so that turning while at high speed still has an impact.
	 *                                 Defaults to 0.
	 */
	@JsonCreator
	public ArcadeOIWithDPad(
			@JsonProperty(required = true) MappedThrottle rotThrottle,
			@JsonProperty(required = true) MappedThrottle fwdThrottle,
			double rotScale,
			double dPadShift,
			boolean invertDPad,
			Joystick gamepad) {
		this.dPadShift = (invertDPad? -1 : 1) * dPadShift;
		this.rotThrottle = rotThrottle;
		this.fwdThrottle = fwdThrottle;
		this.gamepad = gamepad;
		this.rotScale = rotScale;
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
		if (gamepad != null && !(gamepad.getPOV() == -1 || gamepad.getPOV() % 180 == 0)) {
			//Output the shift value
			return gamepad.getPOV() < 180 ? dPadShift : -dPadShift;
		} else {
			//Return the throttle value if it's outside of the deadband.
			return rotThrottle.getValue();
		}
	}
}
