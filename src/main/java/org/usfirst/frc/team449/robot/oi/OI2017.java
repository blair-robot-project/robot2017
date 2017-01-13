package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017 extends OISubsystem{

	private Throttle leftThrottle;
	private Throttle rightThrottle;

	public OI2017(maps.org.usfirst.frc.team449.robot.oi.OI2017Map.OI2017 map){
		super(map.getOi());
		this.map = map;
		leftThrottle = new SmoothedThrottle(new Joystick(map.getLeftStick()), 1);
		rightThrottle = new SmoothedThrottle(new Joystick(map.getRightStick()), 1);
	}

	@Override
	public double getDriveAxisLeft() {
		return -leftThrottle.getValue();
	}

	@Override
	public double getDriveAxisRight() {
		return -rightThrottle.getValue();
	}

	@Override
	public void toggleCamera() {
		//Do Nothing!
	}

	@Override
	protected void initDefaultCommand() {
		//Inheritance is stupid sometimes.
	}
}
