package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import maps.org.usfirst.frc.team449.robot.oi.OI2017TankMap;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.TankOI;
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

	/**
	 * Config map
	 */
	private OI2017TankMap.OI2017Tank map;

	/**
	 * Construct OI2017Map
	 *
	 * @param map config map
	 */
	public OI2017Tank(maps.org.usfirst.frc.team449.robot.oi.OI2017TankMap.OI2017Tank map) {
		this.map = map;

		//Instantiate the sticks.
		Joystick _leftStick = new Joystick(map.getLeftStick());
		Joystick _rightStick = new Joystick(map.getRightStick());
		this.leftThrottle = new SmoothedThrottle(_leftStick, 1);
		this.rightThrottle = new SmoothedThrottle(_rightStick, 1);
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
		// TODO put a deadband
		return leftThrottle.getValue();
	}

	/**
	 * @return throttle to the right motor cluster [-1, 1]
	 */
	@Override
	public double getRightThrottle() {
		// TODO put a deadband
		return rightThrottle.getValue();
	}
}