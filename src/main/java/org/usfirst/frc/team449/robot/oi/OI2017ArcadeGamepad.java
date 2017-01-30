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
		return getVelAxis() + getTurnAxis();
	}

	@Override
	public double getDriveAxisRight() {
		return getVelAxis() - getTurnAxis();
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
			return gamepad.getPOV() < 180 ? SHIFT:-SHIFT;
		} else {
			return 0;
		}
	}
}
