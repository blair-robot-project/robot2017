package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * An object that holds the constants for an angular PID loop.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class AnglePID {
	/**
	 * The PID gains for this loop.
	 */
	private PID PID;

	/**
	 * How many consecutive loops have to be run while within tolerance to be considered on target. Multiply by loop
	 * period of ~20 milliseconds for time.
	 */
	private int toleranceBuffer;

	/**
	 * The maximum number of degrees off from the target at which we can be considered within tolerance.
	 */
	private double absoluteTolerance;

	/**
	 * The minimum output of the loop.
	 */
	private double minimumOutput;

	/**
	 * The maximum output of the loop.
	 */
	private Double maximumOutput;

	/**
	 * The deadband around the setpoint, in degrees, within which no output is given to the motors.
	 */
	private double deadband;

	/**
	 * The maximum angular velocity, in degrees/sec, at which the loop will be entered.
	 */
	private double maxAngularVelToEnterLoop;

	/**
	 * Whether the loop is inverted.
	 */
	private boolean inverted;

	/**
	 * The delay to enter the loop after conditions for entry are met.
	 */
	private double loopEntryDelay;

	/**
	 * Default constructor.
	 *
	 * @param PID                      The PID gains for this loop.
	 * @param toleranceBuffer          How many consecutive loops have to be run while within tolerance to be considered
	 *                                 on target. Multiply by loop period of ~20 milliseconds for time. Defaults to 0.
	 * @param absoluteTolerance        The maximum number of degrees off from the target at which we can be considered
	 *                                 within tolerance.
	 * @param minimumOutput            The minimum output of the loop. Defaults to zero.
	 * @param maximumOutput            The maximum output of the loop. Can be null, and if it is, no maximum output is
	 *                                 used.
	 * @param deadband                 The deadband around the setpoint, in degrees, within which no output is given to
	 *                                 the motors. Defaults to zero.
	 * @param maxAngularVelToEnterLoop The maximum angular velocity, in degrees/sec, at which the loop will be entered.
	 *                                 Defaults to 180.
	 * @param inverted                 Whether the loop is inverted. Defaults to false.
	 * @param loopEntryDelay           The delay to enter the loop after conditions for entry are met. Defaults to
	 *                                 zero.
	 */
	@JsonCreator
	public AnglePID(@JsonProperty(required = true) PID PID,
	                @JsonProperty(required = true) double absoluteTolerance,
	                int toleranceBuffer,
	                double minimumOutput, Double maximumOutput,
	                double deadband,
	                Double maxAngularVelToEnterLoop,
	                boolean inverted,
	                double loopEntryDelay) {
		this.PID = PID;
		this.toleranceBuffer = toleranceBuffer;
		this.absoluteTolerance = absoluteTolerance;
		this.minimumOutput = minimumOutput;
		this.maximumOutput = maximumOutput;
		this.deadband = deadband;
		if (maxAngularVelToEnterLoop == null) {
			maxAngularVelToEnterLoop = 180.;
		}
		this.maxAngularVelToEnterLoop = maxAngularVelToEnterLoop;
		this.inverted = inverted;
		this.loopEntryDelay = loopEntryDelay;
	}

	public org.usfirst.frc.team449.robot.components.PID getPID() {
		return PID;
	}

	public int getToleranceBuffer() {
		return toleranceBuffer;
	}

	public double getAbsoluteTolerance() {
		return absoluteTolerance;
	}

	public double getMinimumOutput() {
		return minimumOutput;
	}

	public Double getMaximumOutput() {
		return maximumOutput;
	}

	public double getDeadband() {
		return deadband;
	}

	public double getMaxAngularVelToEnterLoop() {
		return maxAngularVelToEnterLoop;
	}

	public boolean isInverted() {
		return inverted;
	}

	public double getLoopEntryDelay() {
		return loopEntryDelay;
	}
}
