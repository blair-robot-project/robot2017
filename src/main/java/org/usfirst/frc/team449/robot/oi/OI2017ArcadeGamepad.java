package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import maps.org.usfirst.frc.team449.robot.oi.OI2017Map;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017ArcadeGamepad extends OI2017 {

	static final double SHIFT = 0.3;

	public OI2017ArcadeGamepad(OI2017Map.OI2017 map) {
		super(map);
		rightThrottle = new SmoothedThrottle(new Joystick(5), 0);
	}

	@Override
	public double getDriveAxisLeft() {
		double toRet;

		if (gamepad.getPOV() > 0 && gamepad.getPOV() < 180) {
			toRet = gRight.getValue() - SHIFT;
		} else if (gamepad.getPOV() > 180 && gamepad.getPOV() < 360) {
			toRet = -(gRight.getValue() + SHIFT);
		} else
			toRet = gRight.getValue();
		if (Math.abs(leftThrottle.getValue() - rightThrottle.getValue()) > joystickDeadband)
			return toRet+ leftThrottle.getValue() - rightThrottle.getValue();
		return toRet;
		//		if (Math.abs(leftThrottle.getValue() - rightThrottle.getValue()) > joystickDeadband)
		//			return leftThrottle.getValue() - rightThrottle.getValue();
		//		return 0;
	}

	@Override
	public double getDriveAxisRight() {
		double toRet;
		if (gamepad.getPOV() > 0 && gamepad.getPOV() < 180) {
			toRet = -(gRight.getValue() - SHIFT);
		}else if (gamepad.getPOV() > 180 && gamepad.getPOV() < 360) {
			toRet = gRight.getValue() + SHIFT;
		} else
			toRet = gRight.getValue();

		if (Math.abs(leftThrottle.getValue() + rightThrottle.getValue()) > joystickDeadband)
			return toRet + leftThrottle.getValue() + rightThrottle.getValue();
		return toRet;

		//		return (gRight.getValue() + gLeft.getValue());
		//		if (Math.abs(leftThrottle.getValue() + rightThrottle.getValue()) > joystickDeadband)
		//			return leftThrottle.getValue() + rightThrottle.getValue();
		//		return 0;
	}
}
