package org.usfirst.frc.team449.robot.oi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.components.MappedSmoothedThrottle;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;
import org.usfirst.frc.team449.robot.oi.buttons.CommandButton;

import java.util.List;

/**
 * A simple, two-stick arcade drive OI that uses two distinct joysticks
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class OI2017Arcade extends ArcadeOI {
	/**
	 * Left (rotation control) stick's throttle
	 */
	private MappedSmoothedThrottle rotThrottle;

	/**
	 * Right (fwd/rev control) stick's throttle
	 */
	private MappedSmoothedThrottle velThrottle;

	/**
	 * The button-command mappings for running commands. This only exists to prevent garbage collection.
	 */
	private List<CommandButton> buttons;

	/**
	 * Default constructor
	 *
	 * @param rotThrottle The throttle for rotating the robot.
	 * @param velThrottle The throttle for driving straight.
	 * @param buttons     The button-command mappings for running commands.
	 */
	@JsonCreator
	public OI2017Arcade(@JsonProperty(required = true) MappedSmoothedThrottle rotThrottle,
	                    @JsonProperty(required = true) MappedSmoothedThrottle velThrottle,
	                    List<CommandButton> buttons) {
		this.rotThrottle = rotThrottle;
		this.velThrottle = velThrottle;
		this.buttons = buttons;
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
