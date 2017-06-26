package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MappedSmoothedThrottle;
import org.usfirst.frc.team449.robot.components.MappedThrottle;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;

/**
 * A simple, two-stick arcade drive OI.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SimpleArcadeOI extends ArcadeOI {

	/**
	 * Left (rotation control) stick's throttle
	 */
	@NotNull
	private final MappedThrottle rotThrottle;

	/**
	 * Right (fwd/rev control) stick's throttle
	 */
	@NotNull
	private final MappedThrottle velThrottle;

	/**
	 * Default constructor
	 *
	 * @param rotThrottle The throttle for rotating the robot.
	 * @param velThrottle The throttle for driving straight.
	 */
	@JsonCreator
	public SimpleArcadeOI(@NotNull @JsonProperty(required = true) MappedThrottle rotThrottle,
	                      @NotNull @JsonProperty(required = true) MappedThrottle velThrottle) {
		this.rotThrottle = rotThrottle;
		this.velThrottle = velThrottle;
	}

	/**
	 * @return rotational velocity component
	 */
	public double getRot() {
		return rotThrottle.getValue();
	}

	/**
	 * @return forward velocity component
	 */
	public double getFwd() {
		return velThrottle.getValue();
	}
}
