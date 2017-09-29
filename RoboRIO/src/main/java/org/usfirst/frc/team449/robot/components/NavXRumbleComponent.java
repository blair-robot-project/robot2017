package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kauailabs.navx.frc.AHRS;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.generalInterfaces.rumbleable.Rumbleable;

import java.util.List;

/**
 * A component to rumble controllers based off the acceleration measurements from a NavX.
 */
public class NavXRumbleComponent {

	/**
	 * The NavX to get acceleration measurements from.
	 */
	@NotNull
	private final AHRS navX;

	/**
	 * The things to rumble.
	 */
	@NotNull
	private final List<Rumbleable> rumbleables;

	/**
	 * The minimum acceleration that will trigger rumbling, in Gs. Should be greater than 2, which is the error margin
	 * on the measurement.
	 */
	private final double minAccel;

	/**
	 * The acceleration, in Gs, that's scaled to maximum rumble. All accelerations of greater magnitude are capped at
	 * 1.
	 */
	private final double maxAccel;

	/**
	 * Whether the NavX Y-axis measures forwards-back acceleration or left-right acceleration.
	 */
	private final boolean yIsFrontBack;

	/**
	 * Whether to invert the left-right acceleration measurement.
	 */
	private final boolean invertLeftRight;

	/**
	 * Variables for the per-call rumble calculation representing the directional accelerations. Fields to avoid garbage
	 * collection.
	 */
	private double frontBack, leftRight;

	/**
	 * Variables for the per-call rumble calculation representing the rumble outputs. Fields to avoid garbage
	 * collection.
	 */
	private double left, right;

	/**
	 * Default constructor.
	 *
	 * @param navX            The NavX to get acceleration measurements from.
	 * @param rumbleables     The things to rumble.
	 * @param minAccel        The minimum acceleration that will trigger rumbling, in Gs. Should be greater than 2,
	 *                        which is the error margin on the measurement.
	 * @param maxAccel        The acceleration, in Gs, that's scaled to maximum rumble. All accelerations of greater
	 *                        magnitude are capped at 1.
	 * @param yIsFrontBack    Whether the NavX Y-axis measures forwards-back acceleration or left-right acceleration.
	 *                        Defaults to false.
	 * @param invertLeftRight Whether to invert the left-right acceleration measurement. Defaults to false.
	 */
	@JsonCreator
	public NavXRumbleComponent(@NotNull @JsonProperty(required = true) AHRS navX,
	                           @NotNull @JsonProperty(required = true) List<Rumbleable> rumbleables,
	                           @JsonProperty(required = true) double minAccel,
	                           @JsonProperty(required = true) double maxAccel,
	                           boolean yIsFrontBack,
	                           boolean invertLeftRight) {
		this.navX = navX;
		this.rumbleables = rumbleables;
		this.minAccel = minAccel;
		this.maxAccel = maxAccel;
		this.yIsFrontBack = yIsFrontBack;
		this.invertLeftRight = invertLeftRight;
	}

	/**
	 * Read the NavX acceleration data and rumble the joysticks based off of it.
	 */
	public void rumble() {
		if (yIsFrontBack) {
			frontBack = Math.abs(navX.getWorldLinearAccelY());
			leftRight = navX.getWorldLinearAccelX() * (invertLeftRight ? -1 : 1);
		} else {
			frontBack = Math.abs(navX.getWorldLinearAccelX());
			leftRight = navX.getWorldLinearAccelY() * (invertLeftRight ? -1 : 1);
		}
		//Left is negative accel, so we subtract it from left so that when we're going left, left is bigger and vice versa
		left = frontBack - leftRight;
		right = frontBack + leftRight;

		if (left > minAccel) {
			left = (left - minAccel) / maxAccel;
		} else {
			left = 0;
		}

		if (right > minAccel) {
			right = (right - minAccel) / maxAccel;
		} else {
			right = 0;
		}

		for (Rumbleable rumbleable : rumbleables) {
			rumbleable.rumble(left, right);
		}
	}

}
