package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import maps.org.usfirst.frc.team449.robot.oi.OI2017TankMap;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.TankOI;
import org.usfirst.frc.team449.robot.oi.components.PolyThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by ryant on 2017-01-25.
 */
public class OI2017Tank extends BaseOI implements TankOI {
	private Throttle leftThrottle;
	private Throttle rightThrottle;

	private OI2017TankMap.OI2017Tank map;

	public OI2017Tank(maps.org.usfirst.frc.team449.robot.oi.OI2017TankMap.OI2017Tank map) {
		this.map = map;

		Joystick _leftStick = new Joystick(map.getLeftStick());
		Joystick _rightStick = new Joystick(map.getRightStick());
		this.leftThrottle = new PolyThrottle(_leftStick, 1, 1);
		this.rightThrottle = new PolyThrottle(_rightStick, 1, 1);
	}
	@Override
	public void mapButtons() {
		// Do nothing (no buttons yet)
	}

	@Override
	public double getLeftThrottle() {
		// TODO put a deadband
		return leftThrottle.getValue();
	}

	@Override
	public double getRightThrottle() {
		// TODO put a deadband
		return rightThrottle.getValue();
	}
}
