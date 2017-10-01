package org.usfirst.frc.team449.robot.oi.fieldoriented;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.oi.throttles.Throttle;

/**
 * A field-oriented OI that always points the robot an angle where cosine is positive, i.e. always pointing away from the driver station.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OIFieldOrientedPosCos implements OIFieldOriented{

	/**
	 * The throttle for the X-axis, which points towards the opposing driver station.
	 */
	@NotNull
	private final Throttle xThrottle;

	/**
	 * The throttle for the Y-axis, which points towards the driver's left.
	 */
	@NotNull
	private final Throttle yThrottle;

	/**
	 * The theta value calculated the last time calcValues was called.
	 */
	@Nullable
	private Double theta;

	/**
	 * The velocity value calculated the last time calcValues was called.
	 */
	private double vel;

	/**
	 * The time calcValues was last called, in milliseconds
	 */
	private long timeLastUpdated;

	/**
	 * Variables for the outputs of the x and y throttles. Fields to avoid garbage collection.
	 */
	private double x, y;

	/**
	 * Default constructor
	 *
	 * @param xThrottle The throttle for the X-axis, which points towards the opposing driver station.
	 * @param yThrottle The throttle for the Y-axis, which points towards the driver's left.
	 */
	@JsonCreator
	public OIFieldOrientedPosCos(@NotNull @JsonProperty(required = true) Throttle xThrottle,
	                             @NotNull @JsonProperty(required = true) Throttle yThrottle) {
		this.xThrottle = xThrottle;
		this.yThrottle = yThrottle;
	}

	/**
	 * Calculate the theta and vel values, can be called multiple times per tic but will only execute logic once.
	 */
	protected void calcValues(){
		if (timeLastUpdated != Robot.currentTimeMillis()){
			x = xThrottle.getValue();
			y = yThrottle.getValue();
			//0,0 has no angle so null
			if (x == 0 && y == 0){
				theta = null;
				vel = 0;
			} else {
				//Use atan2 to get angle from -180 to 180
				theta = Math.toDegrees(Math.atan2(y,x));
				if (theta > 90){
					vel = -Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
					theta -= 180;
				} else if (theta < -90){
					vel = -Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
					theta += 180;
				} else {
					vel = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
				}
			}
			timeLastUpdated = Robot.currentTimeMillis();
		}
	}


	/**
	 * Get the absolute angle for the robot to move towards.
	 *
	 * @return An angular setpoint for the robot in degrees, where 0 is pointing at the other alliance's driver station
	 * and 90 is pointing at the left wall when looking out from the driver station.
	 */
	@Override
	@Nullable
	public Double getTheta() {
		calcValues();
		return theta;
	}

	/**
	 * Get the velocity for the robot to go at.
	 *
	 * @return A velocity from [-1, 1].
	 */
	@Override
	public double getVel() {
		calcValues();
		return vel;
	}
}
