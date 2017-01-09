package org.usfirst.frc.team449.robot.oi;

import edu.wpi.first.wpilibj.Joystick;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * Created by blairrobot on 1/9/17.
 */
public class OI2017 extends OISubsystem{

	private Throttle leftThrottle;
	private Throttle righThrottle;

	@Override
	public double getDriveAxisLeft() {
		return 0;
	}

	@Override
	public double getDriveAxisRight() {
		return 0;
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
