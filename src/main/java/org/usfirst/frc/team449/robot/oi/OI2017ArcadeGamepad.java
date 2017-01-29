package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.oi.OI2017Map;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017ArcadeGamepad extends OI2017 {

	static final double SHIFT = 0.3;
	private Throttle turnThrottle;
	private Throttle velThrottle;

	public OI2017ArcadeGamepad(OI2017Map.OI2017 map) {
		super(map);
		turnThrottle = gLeft;
		velThrottle = gRight;
	}

	@Override
	public double getDriveAxisLeft() {
		double toRet = 0;

		if (gamepad.getPOV() > 0 && gamepad.getPOV() < 180) {
			toRet = -velThrottle.getValue() + SHIFT;
		} else if (gamepad.getPOV() > 180 && gamepad.getPOV() < 360) {
			toRet = -velThrottle.getValue() - SHIFT;
		} else if (Math.abs(-velThrottle.getValue() + turnThrottle.getValue()) > joystickDeadband) {
			toRet = -velThrottle.getValue() + turnThrottle.getValue();
		}

		return toRet;
	}

	@Override
	public double getDriveAxisRight() {
		double toRet = 0;

		if (gamepad.getPOV() > 0 && gamepad.getPOV() < 180) {
			toRet = -velThrottle.getValue() - SHIFT;
		}else if (gamepad.getPOV() > 180 && gamepad.getPOV() < 360) {
			toRet = -velThrottle.getValue() + SHIFT;
		} else if (Math.abs(-velThrottle.getValue() - turnThrottle.getValue()) > joystickDeadband) {
			toRet = -velThrottle.getValue() - turnThrottle.getValue();
		}

		SmartDashboard.putNumber("Turn value", turnThrottle.getValue());
		SmartDashboard.putNumber("Vel value", velThrottle.getValue());

		return toRet;
	}
}
