package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;

/**
 * Created by noah on 6/25/17.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class MappedAHRS extends AHRS {

	@JsonCreator
	public MappedAHRS(@JsonProperty(required = true) SPI.Port port) {
		super(port);
	}
}
