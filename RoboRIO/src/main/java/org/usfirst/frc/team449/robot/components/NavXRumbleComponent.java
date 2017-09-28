package org.usfirst.frc.team449.robot.components;

import com.kauailabs.navx.frc.AHRS;
import org.usfirst.frc.team449.robot.generalInterfaces.rumbleable.Rumbleable;

import java.util.List;

public class NavXRumbleComponent {

	private final AHRS navX;

	private final List<Rumbleable> rumbleables;

	private final boolean yIsFrontBack;

	private final boolean invertLeftRight;

	private final double minAccel;

	private final double maxAccel;

	public void rumble(){
		double frontBack;
		double leftRight;
		if (yIsFrontBack){
			frontBack = Math.abs(navX.getWorldLinearAccelY());
			leftRight = navX.getWorldLinearAccelX() * (invertLeftRight ? -1 : 1);
		} else {
			frontBack = Math.abs(navX.getWorldLinearAccelX());
			leftRight = navX.getWorldLinearAccelY() * (invertLeftRight ? -1 : 1);
		}
		//Left is negative accel, so we subtract it from left so that when we're going left, left is bigger and vice versa
		double left = frontBack - leftRight;
		double right = frontBack + leftRight;


	}

}
