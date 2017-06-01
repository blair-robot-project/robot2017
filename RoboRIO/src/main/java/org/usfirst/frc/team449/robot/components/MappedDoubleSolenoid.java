package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import maps.org.usfirst.frc.team449.robot.components.ModuleDoubleSolenoidMap;

/**
 * A wrapper on the {@link DoubleSolenoid} that can be constructed from a map object.
 */
public class MappedDoubleSolenoid extends DoubleSolenoid {

	/**
	 * Default constructor.
	 *
	 * @param map A map containing module number, forward port, and reverse port.
	 */
	public MappedDoubleSolenoid(ModuleDoubleSolenoidMap.ModuleDoubleSolenoid map) {
		super(map.getModule(), map.getForward(), map.getReverse());
	}
}
