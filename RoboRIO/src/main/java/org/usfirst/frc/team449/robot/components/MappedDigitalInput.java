package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.DigitalInput;
import maps.org.usfirst.frc.team449.robot.components.DigitalInputMap;

import java.util.ArrayList;
import java.util.List;

/**
 * A series of roboRIO digital input pins.
 */
public class MappedDigitalInput {

	/**
	 * The digitalInputs this class represents
	 */
	private List<DigitalInput> digitalInputs;

	/**
	 * Construct a MappedDigitalInput.
	 *
	 * @param map the map to construct this from.
	 */
	public MappedDigitalInput(DigitalInputMap.DigitalInput map) {
		digitalInputs = new ArrayList<>();
		for (int portNum : map.getPortList()) {
			DigitalInput tmp = new DigitalInput(portNum);
			digitalInputs.add(tmp);
		}
	}

	/**
	 * Get the status of each pin specified in the map, in the order they were specified.
	 *
	 * @return A list of booleans where 1 represents the input receiving a signal and 0 represents no signal.
	 */
	public List<Boolean> getStatus() {
		List<Boolean> digitalValues = new ArrayList<>();
		for (DigitalInput digitalInput : digitalInputs) {
			digitalValues.add(!digitalInput.get());
		}
		return digitalValues;
	}
}