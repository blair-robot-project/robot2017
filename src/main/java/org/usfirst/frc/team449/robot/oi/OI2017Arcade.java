package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import maps.org.usfirst.frc.team449.robot.oi.OI2017Map;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.NavXDriveStraight;
import org.usfirst.frc.team449.robot.oi.components.PolyThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017Arcade extends OI2017 {

	public OI2017Arcade(OI2017Map.OI2017 map) {
		super(map);
	}

	@Override
	public double getDriveAxisLeft() {
		if (Math.abs(leftThrottle.getValue() - rightThrottle.getValue()) > minimumOutput)
			return leftThrottle.getValue() - rightThrottle.getValue();
		return 0;
	}

	@Override
	public double getDriveAxisRight() {
		if (Math.abs(leftThrottle.getValue() + rightThrottle.getValue()) > minimumOutput)
			return leftThrottle.getValue() + rightThrottle.getValue();
		return 0;
	}
}
