package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Wrapper for an analog pressure sensor that returns a voltage linearly proportional to pressure.
 * Created by sam on 1/27/17.
 */
public class PressureSensor extends Component {
    public AnalogInput sensor;
    double kM, kB;

    public PressureSensor(maps.org.usfirst.frc.team449.robot.components.AnalogPressureSensorMap.AnalogPressureSensor map){
        sensor = new AnalogInput(map.getPort());
        sensor.setOversampleBits(map.getOversampleBits());
        sensor.setAverageBits(map.getAverageBits());
        kM = map.getKProp();
        kB = map.getKOffset();
    }

    /**
     * Returns the pressure measured by the sensor.
     * @return pressure in PSI
     */
    public double getPressure(){
        return kM * sensor.getAverageVoltage() + kB;
    }
}
