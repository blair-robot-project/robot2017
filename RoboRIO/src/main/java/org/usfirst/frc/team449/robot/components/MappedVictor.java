package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.VictorSP;
import maps.org.usfirst.frc.team449.robot.components.MotorMap;

/**
 * A wrapper for a VictorSP allowing it to be easily constructed from a map object.
 */
public class MappedVictor extends VictorSP {

	/**
	 * Construct a {@link VictorSP} from a {@link MotorMap.Motor}.
	 * @param map a motor map specifying port and inversion.
	 */
	public MappedVictor(MotorMap.Motor map) {
		super(map.getPort());
		this.setInverted(map.getInverted());
	}
}
