package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import org.jetbrains.annotations.Contract;

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
		this.reset();
	}

	/**
	 * Convert from gs (acceleration due to gravity) to feet/(second^2).
	 *
	 * @param accelGs An acceleration in gs.
	 * @return That acceleration in feet/(sec^2)
	 */
	@Contract(pure = true)
	public static double gsToFeetPerSecondSquared(double accelGs) {
		return accelGs * 32.17; //Wolfram alpha said so
	}
}
