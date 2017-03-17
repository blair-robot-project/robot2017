package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Wrapper for an analog pressure sensor that returns a voltage linearly proportional to pressure.
 */
public class PressureSensor extends Component {
	public AnalogInput sensor;

	public PressureSensor(maps.org.usfirst.frc.team449.robot.components.AnalogPressureSensorMap.AnalogPressureSensor
			                      map) {
		sensor = new AnalogInput(map.getPort());
		sensor.setOversampleBits(map.getOversampleBits());
		sensor.setAverageBits(map.getAverageBits());
	}

	/**
	 * Returns the pressure measured by the sensor.
	 *
	 * @return pressure in PSI
	 */
	public double getPressure() {
		return 50.0 * sensor.getAverageVoltage() - 25.0;    //these are constants given by REV, assuming 5.0V in
	}
}
