package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * A simple object that hold PID constants.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class PID {
	/**
	 * The proportional gain.
	 */
	private double kP;

	/**
	 * The integral gain.
	 */
	private double kI;

	/**
	 * The derivative gain.
	 */
	private double kD;

	/**
	 * Default constructor
	 * @param kP Proportional gain. Defaults to zero.
	 * @param kI Integral gain. Defaults to zero.
	 * @param kD Derivative gain. Defaults to zero.
	 */
	@JsonCreator
	public PID(double kP, double kI, double kD) {
		this.kP = kP;
		this.kI = kI;
		this.kD = kD;
	}

	/**
	 * Getter for P gain.
	 * @return Proportional gain.
	 */
	public double getP() {
		return kP;
	}

	/**
	 * Getter for I gain.
	 * @return Integral gain.
	 */
	public double getI() {
		return kI;
	}

	/**
	 * Getter for D gain.
	 * @return Derivative gain.
	 */
	public double getD() {
		return kD;
	}
}
