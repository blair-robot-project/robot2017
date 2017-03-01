package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import maps.org.usfirst.frc.team449.robot.oi.OI2017ArcadeMap;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.ArcadeOI;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * A simple, two-stick arcade drive OI that uses two distinct joysticks
 */
public class OI2017Arcade extends BaseOI implements ArcadeOI {
	/**
	 * Left (rotation control) stick's throttle
	 */
	private Throttle rotThrottle;
	/**
	 * Right (fwd/rev control) stick's throttle
	 */
	private Throttle velThrottle;

	/**
	 * The map for this object.
	 */
	private OI2017ArcadeMap.OI2017Arcade map;

	/**
	 * Construct an OI2017Arcade
	 *
	 * @param map config map
	 */
	public OI2017Arcade(maps.org.usfirst.frc.team449.robot.oi.OI2017ArcadeMap.OI2017Arcade map) {
		this.map = map;

		//Instantiate the sticks
		Joystick _leftStick = new Joystick(map.getLeftStick());
		Joystick _rightStick = new Joystick(map.getRightStick());
		this.rotThrottle = new SmoothedThrottle(_leftStick, 1);
		this.velThrottle = new SmoothedThrottle(_rightStick, 1);
	}

	/**
	 * Map the buttons (call this after all subsytems are constructed)
	 */
	@Override
	public void mapButtons() {
		// Do nothing
	}

	/**
	 * @return rotational velocity component
	 */
	public double getRot() {
		return rotThrottle.getValue();
	}

	/**
	 * @return forward velocity component
	 */
	public double getFwd() {
		return velThrottle.getValue();
	}
}
