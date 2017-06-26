package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MappedSmoothedThrottle;
import org.usfirst.frc.team449.robot.components.MappedThrottle;
import org.usfirst.frc.team449.robot.interfaces.oi.TankOI;

/**
 * A simple tank drive, where each joystick controls a side of the robot.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SimpleTankOI extends TankOI {

	/**
	 * The left throttle
	 */
	@NotNull
	private final MappedThrottle leftThrottle;

	/**
	 * The right throttle
	 */
	@NotNull
	private final MappedThrottle rightThrottle;

	/**
	 * The difference between left and right input within which the driver is considered to be trying to drive straight.
	 */
	private final double commandingStraightTolerance;

	/**
	 * Default constructor
	 *
	 * @param leftThrottle  The throttle for controlling the velocity of the left side of the drive.
	 * @param rightThrottle The throttle for controlling the velocity of the right side of the drive.
	 * @param commandingStraightTolerance The difference between left and right input within which the driver is considered to be trying to drive straight. Defaults to 0.
	 */
	@JsonCreator
	public SimpleTankOI(@NotNull @JsonProperty(required = true) MappedThrottle leftThrottle,
	                    @NotNull @JsonProperty(required = true) MappedThrottle rightThrottle,
	                    double commandingStraightTolerance) {
		this.leftThrottle = leftThrottle;
		this.rightThrottle = rightThrottle;
		this.commandingStraightTolerance = commandingStraightTolerance;
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

	/**
	 * Whether the driver is trying to drive straight.
	 *
	 * @return True if the driver is trying to drive straight, false otherwise.
	 */
	@Override
	public boolean commandingStraight() {
		return Math.abs(leftThrottle.getValue()-rightThrottle.getValue()) <= commandingStraightTolerance;
	}
}