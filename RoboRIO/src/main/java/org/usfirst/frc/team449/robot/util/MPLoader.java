package org.usfirst.frc.team449.robot.util;

import com.ctre.CANTalon;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;

/**
 * Created by BlairRobot on 2017-03-15.
 */
public class MPLoader {

	public static void loadTopLevel(MotionProfileData data, RotPerSecCANTalonSRX talon, double wheelDiameter) {
		// Fill the Talon's buffer with points
		talon.canTalon.disable();
		talon.canTalon.clearMotionProfileHasUnderrun();
		talon.canTalon.clearMotionProfileTrajectories();
		CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();
		for (int i = 0; i < data.data.length; ++i) {
			// Set all the fields of the profile point
			point.position = feetToNative(data.data[i][0], talon.encoderCPR, wheelDiameter);
			point.velocity = feetPerSecToNative(data.data[i][1], talon, wheelDiameter);
			point.timeDurMs = (int) (data.data[i][2] * 1000.);
			point.profileSlotSelect = 1;    // gain selection
			point.velocityOnly = false;  // true => no position servo just velocity feedforward
			point.zeroPos = i == 0; // If its the first point, zeroPos  =  true
			point.isLastPoint = (i + 1) == data.data.length; // If its the last point, isLastPoint = true

			// Send the point to the Talon's buffer
			if (!talon.canTalon.pushMotionProfileTrajectory(point)) {
				Logger.addEvent("Buffer full!", MPLoader.class);
				break;
			}
		}
	}

	public static double nativeToFeet(double nativeUnits, int encoderCPR, double wheelDiameter) {
		double rotations = nativeUnits / (encoderCPR * 4);
		return rotations * (wheelDiameter * Math.PI);
	}

	public static double feetToNative(double feet, int encoderCPR, double wheelDiameter) {
		double rotations = feet / (wheelDiameter * Math.PI);
		return rotations * (encoderCPR * 4);
	}

	public static double feetPerSecToNative(double feet, RotPerSecCANTalonSRX talon, double wheelDiameter) {
		return talon.RPSToNative(feet / (wheelDiameter * Math.PI));
	}

	public static double nativeToFeetPerSec(double nativeUnits, RotPerSecCANTalonSRX talon, double wheelDiameter) {
		return talon.nativeToRPS(nativeUnits) * (wheelDiameter * Math.PI);
	}
}
