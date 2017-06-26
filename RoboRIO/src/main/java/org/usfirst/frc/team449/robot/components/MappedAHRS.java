package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

/**
 * A Jackson-compatible wrapper for the NavX.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedAHRS extends AHRS {

	/**
	 * Default constructor.
	 *
	 * @param port The port the NavX is plugged into. It seems like only kMXP (the port on the RIO) works.
	 */
	@JsonCreator
	public MappedAHRS(@JsonProperty(required = true) SPI.Port port) {
		super(port);
	}
}
