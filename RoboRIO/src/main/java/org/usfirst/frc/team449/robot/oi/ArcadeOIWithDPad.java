package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Joystick;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.MappedJoystick;
import org.usfirst.frc.team449.robot.components.MappedThrottle;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;
import org.usfirst.frc.team449.robot.util.Polynomial;

/**
 * Created by noahg on 18-Jun-17.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ArcadeOIWithDPad extends ArcadeOI {

	/**
	 * How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the turning stick that
	 * much of the way
	 */
	private final double dPadShift;

	/**
	 * The throttle wrapper for the stick controlling turning velocity
	 */
	@NotNull
	private final MappedThrottle rotThrottle;

	/**
	 * The throttle wrapper for the stick controlling linear velocity
	 */
	@NotNull
	private final MappedThrottle fwdThrottle;

	/**
	 * The controller with the D-pad. Can be null if not using D-pad.
	 */
	@Nullable
	private final Joystick gamepad;

	/**
	 * Scaling, from [0, 1], that the rotational throttle decreases the forwards throttle by. Used so that turning while
	 * at high speed still has an impact.
	 */
	private final double scaleFwdByRotCoefficient;

	@Nullable
	private final Polynomial scaleRotByFwdPoly;

	/**
	 * Default constructor
	 *
	 * @param gamepad     The gamepad containing the joysticks and buttons. Can be null if not using the D-pad.
	 * @param rotThrottle The throttle for rotating the robot.
	 * @param fwdThrottle The throttle for driving the robot straight.
	 * @param invertDPad  Whether or not to invert the D-pad. Defaults to false.
	 * @param dPadShift   How fast the dPad should turn the robot, on [0, 1]. Defaults to 0.
	 * @param scaleFwdByRotCoefficient    Scaling, from [0, 1], that the rotational throttle decreases the forwards
	 *                    throttle by. Used so that turning while at high speed still has an impact.
	 *                    Defaults to 0.
	 */
	@JsonCreator
	public ArcadeOIWithDPad(
			@NotNull @JsonProperty(required = true) MappedThrottle rotThrottle,
			@NotNull @JsonProperty(required = true) MappedThrottle fwdThrottle,
			double scaleFwdByRotCoefficient,
			double dPadShift,
			boolean invertDPad,
			@Nullable MappedJoystick gamepad,
			@Nullable Polynomial scaleRotByFwdPoly) {
		this.dPadShift = (invertDPad ? -1 : 1) * dPadShift;
		this.rotThrottle = rotThrottle;
		this.fwdThrottle = fwdThrottle;
		this.gamepad = gamepad;
		this.scaleFwdByRotCoefficient = scaleFwdByRotCoefficient;
		this.scaleRotByFwdPoly = scaleRotByFwdPoly;
	}

	/**
	 * The output of the throttle controlling linear velocity, smoothed and adjusted according to what type of
	 * joystick it is.
	 *
	 * @return The processed stick output, sign-adjusted so 1 is forward and -1 is backwards.
	 */
	public double getFwd() {
		//Scale based on rotational throttle for more responsive turning at high speed
		return fwdThrottle.getValue() * (1 - scaleFwdByRotCoefficient * getRot());
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
			if (scaleRotByFwdPoly != null){
				return rotThrottle.getValue() * scaleRotByFwdPoly.get(fwdThrottle.getValue());
			}
			return rotThrottle.getValue();
		}
	}
}
