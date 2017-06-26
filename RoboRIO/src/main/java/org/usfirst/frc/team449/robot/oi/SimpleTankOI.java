package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MappedSmoothedThrottle;
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
	private final MappedSmoothedThrottle leftThrottle;

	/**
	 * The right throttle
	 */
	@NotNull
	private final MappedSmoothedThrottle rightThrottle;

	/**
	 * Default constructor
	 *
	 * @param leftThrottle  The throttle for controlling the velocity of the left side of the drive.
	 * @param rightThrottle The throttle for controlling the velocity of the right side of the drive.
	 */
	@JsonCreator
	public SimpleTankOI(@NotNull @JsonProperty(required = true) MappedSmoothedThrottle leftThrottle,
	                    @NotNull @JsonProperty(required = true) MappedSmoothedThrottle rightThrottle) {
		this.leftThrottle = leftThrottle;
		this.rightThrottle = rightThrottle;
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