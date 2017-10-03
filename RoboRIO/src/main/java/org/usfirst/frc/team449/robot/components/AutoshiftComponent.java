package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.other.BufferTimer;

import java.util.function.Consumer;

/**
 * A component class for autoshifting.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AutoshiftComponent {

	/**
	 * The speed setpoint at the upshift break
	 */
	private final double upshiftSpeed;

	/**
	 * The speed setpoint at the downshift break
	 */
	private final double downshiftSpeed;

	/**
	 * The robot isn't eligible to shift again for this many milliseconds after upshifting.
	 */
	private final long cooldownAfterUpshift;

	/**
	 * The robot isn't eligible to shift again for this many milliseconds after downshifting.
	 */
	private final long cooldownAfterDownshift;

	/**
	 * BufferTimers for shifting that make it so all the other conditions to shift must be met for some amount of time
	 * before shifting actually happens.
	 */
	@NotNull
	private final BufferTimer upshiftBufferTimer, downshiftBufferTimer;

	/**
	 * The forward velocity setpoint (on a 0-1 scale) below which we stay in low gear
	 */
	private final double upshiftFwdThresh;

	/**
	 * The time we last upshifted (milliseconds)
	 */
	private long timeLastUpshifted;

	/**
	 * The time we last downshifted (milliseconds)
	 */
	private long timeLastDownshifted;

	/**
	 * Default constructor
	 *
	 * @param upshiftSpeed           The minimum speed both sides the drive must be going at to shift to high gear.
	 * @param downshiftSpeed         The maximum speed both sides must be going at to shift to low gear.
	 * @param upshiftBufferTimer     Buffer timer for upshifting.
	 * @param downshiftBufferTimer   Buffer timer for downshifting.
	 * @param cooldownAfterDownshift The minimum time, in seconds, between downshifting and then upshifting again.
	 *                               Defaults to 0.
	 * @param cooldownAfterUpshift   The minimum time, in seconds, between upshifting and then downshifting again.
	 *                               Defaults to 0.
	 * @param upshiftFwdThresh       The minimum amount the forward joystick must be pushed forward in order to upshift,
	 *                               on [0, 1]. Defaults to 0.
	 */
	@JsonCreator
	public AutoshiftComponent(@JsonProperty(required = true) double upshiftSpeed,
	                          @JsonProperty(required = true) double downshiftSpeed,
	                          @NotNull @JsonProperty(required = true) BufferTimer upshiftBufferTimer,
	                          @NotNull @JsonProperty(required = true) BufferTimer downshiftBufferTimer,
	                          double upshiftFwdThresh,
	                          double cooldownAfterUpshift,
	                          double cooldownAfterDownshift) {
		this.upshiftSpeed = upshiftSpeed;
		this.downshiftSpeed = downshiftSpeed;
		this.upshiftFwdThresh = upshiftFwdThresh;
		this.cooldownAfterUpshift = (long) (cooldownAfterUpshift * 1000.);
		this.cooldownAfterDownshift = (long) (cooldownAfterDownshift * 1000.);
		this.upshiftBufferTimer = upshiftBufferTimer;
		this.downshiftBufferTimer = downshiftBufferTimer;
	}

	/**
	 * Determine whether the robot should downshift.
	 *
	 * @param forwardThrottle The forwards throttle, on [-1, 1].
	 * @param leftVel         The velocity of the left side of the drive.
	 * @param rightVel        The velocity of the right side of the drive.
	 * @return True if the drive should downshift, false otherwise.
	 */
	private boolean shouldDownshift(double forwardThrottle, double leftVel, double rightVel) {
		//We should shift if we're going slower than the downshift speed
		boolean okToShift = Math.max(Math.abs(leftVel), Math.abs(rightVel)) < downshiftSpeed;
		//Or if we're just turning in place.
		okToShift = okToShift || (forwardThrottle == 0);
		//Or commanding a low speed.
		okToShift = okToShift || (Math.abs(forwardThrottle) < upshiftFwdThresh);
		//But we can only shift if we're out of the cooldown period.
		okToShift = okToShift && Robot.currentTimeMillis() - timeLastUpshifted > cooldownAfterUpshift;

		//We use a BufferTimer so we only shift if the conditions are met for a specific continuous interval.
		// This avoids brief blips causing shifting.
		okToShift = downshiftBufferTimer.get(okToShift);

		//Record the time if we do decide to shift.
		if (okToShift) {
			timeLastDownshifted = Robot.currentTimeMillis();
		}
		return okToShift;
	}

	/**
	 * Determine whether the robot should upshift.
	 *
	 * @param forwardThrottle The forwards throttle, on [-1, 1].
	 * @param leftVel         The velocity of the left side of the drive.
	 * @param rightVel        The velocity of the right side of the drive.
	 * @return True if the drive should upshift, false otherwise.
	 */
	private boolean shouldUpshift(double forwardThrottle, double leftVel, double rightVel) {
		//We should shift if we're going faster than the upshift speed...
		boolean okToShift = Math.min(Math.abs(leftVel), Math.abs(rightVel)) > upshiftSpeed;
		//AND the driver's trying to go forward fast.
		okToShift = okToShift && Math.abs(forwardThrottle) > upshiftFwdThresh;
		//But we can only shift if we're out of the cooldown period.
		okToShift = okToShift && Robot.currentTimeMillis() - timeLastDownshifted > cooldownAfterDownshift;

		//We use a BufferTimer so we only shift if the conditions are met for a specific continuous interval.
		// This avoids brief blips causing shifting.
		okToShift = upshiftBufferTimer.get(okToShift);
		if (okToShift) {
			timeLastUpshifted = Robot.currentTimeMillis();
		}
		return okToShift;
	}

	/**
	 * Determine if the subsystem should shift, and if yes, do the shifting.
	 *
	 * @param forwardThrottle The forwards throttle, on [-1, 1].
	 * @param leftVel         The velocity of the left side of the drive.
	 * @param rightVel        The velocity of the right side of the drive.
	 * @param shift           The function to actually shift gears.
	 */
	public void autoshift(double forwardThrottle, double leftVel, double rightVel, Consumer<Integer> shift) {
		if (shouldDownshift(forwardThrottle, leftVel, rightVel)) {
			shift.accept(Shiftable.gear.LOW.getNumVal());
			System.out.println("Downshifted");
		} else if (shouldUpshift(forwardThrottle, leftVel, rightVel)) {
			shift.accept(Shiftable.gear.HIGH.getNumVal());
			System.out.println("Upshifted");
		}
	}
}
