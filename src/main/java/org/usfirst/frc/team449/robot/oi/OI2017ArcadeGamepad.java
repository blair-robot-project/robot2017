package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.oi.OI2017Map;
import org.usfirst.frc.team449.robot.oi.components.PolyThrottle;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017ArcadeGamepad extends OI2017 {

	static final double SHIFT = 0.1;
	private Throttle turnThrottle;
	private Throttle velThrottle;

	public OI2017ArcadeGamepad(OI2017Map.OI2017 map) {
		super(map);
		turnThrottle = new PolyThrottle(gamepad, map.getGamepadLeftAxis(), 2);
		velThrottle = gRight;
	}

	@Override
	public double getDriveAxisLeft() {
		double toRet = 0;

		if (gamepad.getPOV() > 0 && gamepad.getPOV() < 180) {
			toRet = getVelAxis() + SHIFT;
		} else if (gamepad.getPOV() > 180 && gamepad.getPOV() < 360) {
			toRet = getVelAxis() - SHIFT;
		} else if (Math.abs(getVelAxis() + turnThrottle.getValue()) > joystickDeadband) {
			toRet = getVelAxis() + turnThrottle.getValue();
		}

		return toRet;
	}

	@Override
	public double getDriveAxisRight() {
		double toRet = 0;

		if (gamepad.getPOV() > 0 && gamepad.getPOV() < 180) {
			toRet = getVelAxis() - SHIFT;
		}else if (gamepad.getPOV() > 180 && gamepad.getPOV() < 360) {
			toRet = getVelAxis() + SHIFT;
		} else if (Math.abs(getVelAxis() - turnThrottle.getValue()) > joystickDeadband) {
			toRet = getVelAxis() - turnThrottle.getValue();
		}

		SmartDashboard.putNumber("Turn value", turnThrottle.getValue());
		SmartDashboard.putNumber("Vel value", velThrottle.getValue());

		return toRet;
	}

	public double getVelAxis(){
		if (Math.abs(velThrottle.getValue()) > joystickDeadband) {
			return -velThrottle.getValue();
		} else {
			return 0;
		}
	}

	public double getTurnAxis(){
		if ((gamepad.getPOV() == -1 || gamepad.getPOV()%180 == 0) && Math.abs(turnThrottle.getValue()) > joystickDeadband) {
			return turnThrottle.getValue();
		} else if (!(gamepad.getPOV() == -1 || gamepad.getPOV()%180 == 0)){
			return gamepad.getPOV() < 180 ? 1:-1;
		} else {
			return 0;
		}
	}
}
