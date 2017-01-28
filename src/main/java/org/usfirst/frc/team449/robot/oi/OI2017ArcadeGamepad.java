package org.usfirst.frc.team449.robot.oi;

import maps.org.usfirst.frc.team449.robot.oi.OI2017Map;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017ArcadeGamepad extends OI2017 {

	static final double SHIFT = 0.3;

	public OI2017ArcadeGamepad(OI2017Map.OI2017 map) {
		super(map);
	}

	@Override
	public double getDriveAxisLeft() {

		if (gamepad.getPOV() > 0 && gamepad.getPOV() < 180) {
			return gRight.getValue() - SHIFT;
		}
		if (gamepad.getPOV() > 180 && gamepad.getPOV() < 360) {
			return -(gRight.getValue() + SHIFT);
		}
		return gRight.getValue();
		//		if (Math.abs(leftThrottle.getValue() - rightThrottle.getValue()) > joystickDeadband)
		//			return leftThrottle.getValue() - rightThrottle.getValue();
		//		return 0;
	}

	@Override
	public double getDriveAxisRight() {
		if (gamepad.getPOV() > 0 && gamepad.getPOV() < 180) {
			return -(gRight.getValue() - SHIFT);
		}
		if (gamepad.getPOV() > 180 && gamepad.getPOV() < 360) {
			return gRight.getValue() + SHIFT;
		}
		return gRight.getValue();

		//		return (gRight.getValue() + gLeft.getValue());
		//		if (Math.abs(leftThrottle.getValue() + rightThrottle.getValue()) > joystickDeadband)
		//			return leftThrottle.getValue() + rightThrottle.getValue();
		//		return 0;
	}
}
