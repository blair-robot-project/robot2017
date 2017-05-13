package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team449.robot.interfaces.oi.TankOI;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * A simple tank drive, where each joystick controls a side of the robot.
 */
public class OI2017Tank extends BaseOI implements TankOI {
	/**
	 * The left throttle
	 */
	private Throttle leftThrottle;
	/**
	 * The right throttle
	 */
	private Throttle rightThrottle;

	private double deadband;

	/**
	 * Construct OI2017Map
	 *
	 * @param map config map
	 */
	public OI2017Tank(maps.org.usfirst.frc.team449.robot.oi.OI2017TankMap.OI2017Tank map) {
		//Instantiate the sticks.
		Joystick leftStick = new Joystick(map.getLeftStick());
		Joystick rightStick = new Joystick(map.getRightStick());
		leftThrottle = new SmoothedThrottle(leftStick, 1);
		rightThrottle = new SmoothedThrottle(rightStick, 1);
		deadband = map.getDeadband();
	}

	/**
	 * Map all buttons to commands. Should only be run after all subsystems have been instantiated.
	 */
	@Override
	public void mapButtons() {
		// Do nothing (no buttons yet)
	}

	/**
	 * @return throttle to the left motor cluster [-1, 1]
	 */
	@Override
	public double getLeftThrottle() {
		double value = leftThrottle.getValue();
		if(value <= deadband){
			value = 0;
		}
		return value;
	}

	/**
	 * @return throttle to the right motor cluster [-1, 1]
	 */
	@Override
	public double getRightThrottle() {
		double value = rightThrottle.getValue();
		if(value <= deadband){
			value = 0;
		}
		return value;
	}
}