package org.usfirst.frc.team449.robot.oi;

import maps.org.usfirst.frc.team449.robot.oi.OI2017Map;
import org.usfirst.frc.team449.robot.oi.components.PolyThrottle;
import org.usfirst.frc.team449.robot.oi.components.SmoothedThrottle;
import org.usfirst.frc.team449.robot.oi.components.Throttle;

/**
 * An OI for using an Xbox-style controller for an arcade drive, where one stick controls forward velocity and the other
 * controls turning velocity.
 */
public class OI2017ArcadeGamepad extends OI2017 {

	//How much the D-pad moves the robot rotationally on a 0 to 1 scale, equivalent to pushing the turning stick that much of the way.
	private static final double SHIFT = 0.1;
	//The throttle wrapper for the stick controlling turning velocity.
	private Throttle turnThrottle;
	//The throttle wrapper for the stick controlling linear velocity.
	private Throttle velThrottle;

	public OI2017ArcadeGamepad(OI2017Map.OI2017 map) {
		super(map);
		//This is just to give the sticks better names and allow quickly swapping which is which according to driver preference.
		turnThrottle = gLeft;
		velThrottle = gRight;
	}

	/**
	 * Calculates the output to be given to the left side of the robot.
	 * @return The output to the left side of the robot, on a -1 to 1 scale.
	 */
	@Override
	public double getDriveAxisLeft() {
		//We use addition because right is positive for turnAxis, and to turn right you move the left side forward.
		return clipToOne(getVelAxis() + getTurnAxis());
	}

	/**
	 * Calculates the output to be given to the right side of the robot.
	 * @return The output to the right side of the robot, on a -1 to 1 scale.
	 */
	@Override
	public double getDriveAxisRight() {
		//We use subtraction because right is positive for turnAxis, and to turn right you move the right side backwards.
		return clipToOne(getVelAxis() - getTurnAxis());
	}

	/**
	 * The output of the throttle controlling linear velocity, smoothed and adjusted according to what type of joystick it is.
	 * @return The processed stick output, sign-adjusted so 1 is forward and -1 is backwards.
	 */
	public double getVelAxis(){
		if (Math.abs(velThrottle.getValue()) > joystickDeadband) {
			return -velThrottle.getValue();
		} else {
			return 0;
		}
	}

	/**
	 * Get the output of the D-pad or turning joystick, whichever is in use. If both are in use, the D-pad takes preference.
	 * @return The processed stick or D-pad output, sign-adjusted so 1 is right and -1 is left.
	 */
	public double getTurnAxis(){
		if ((gamepad.getPOV() == -1 || gamepad.getPOV()%180 == 0) && (Math.abs(turnThrottle.getValue()) > joystickDeadband)) {
			return turnThrottle.getValue();
		} else if (!(gamepad.getPOV() == -1 || gamepad.getPOV()%180 == 0)){
			return gamepad.getPOV() < 180 ? SHIFT:-SHIFT;
		} else {
			return 0;
		}
	}

	/**
	 * Simple helper function for clipping output to the -1 to 1 scale.
	 * @param in The number to be processed.
	 * @return That number, clipped to 1 if it's greater than 1 or clipped to -1 if it's less than -1.
	 */
	private static double clipToOne(double in){
		if (in > 1)
			return 1;
		else if (in < -1)
			return -1;
		else
			return in;
	}
}
