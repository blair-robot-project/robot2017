package org.usfirst.frc.team449.robot.util;

import com.ctre.CANTalon;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;

/**
 * Utility class for loading points into a CANTalon's motion profile buffer.
 */
public class CANTalonMPLoader {

	/**
	 * Loads the given motion profile onto the given Talon's API-level buffer.
	 * @param data The profile to load.
	 * @param talon The talon to load it into.
	 */
	public static void loadTopLevel(MotionProfileData data, RotPerSecCANTalonSRX talon) {
		//Clear all the MP-related stuff on talon
		talon.canTalon.disable();
		talon.canTalon.clearMotionProfileHasUnderrun();
		talon.canTalon.clearMotionProfileTrajectories();

		//Instantiate the point outside the loop to avoid garbage collection.
		CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();

		//Set parameters that are true for all points
		point.profileSlotSelect = 1;    // gain selection
		point.velocityOnly = false;  // true => no position servo just velocity feedforward

		for (int i = 0; i < data.data.length; ++i) {
			// Set all the fields of the profile point
			point.position = talon.feetToNative(data.data[i][0]);
			point.velocity = talon.feetPerSecToNative(data.data[i][1]);
			point.timeDurMs = (int) (data.data[i][2] * 1000.);
			point.zeroPos = i == 0; // If its the first point, set the encoder position to 0.
			point.isLastPoint = (i + 1) == data.data.length; // If its the last point, isLastPoint = true

			// Send the point to the Talon's buffer
			if (!talon.canTalon.pushMotionProfileTrajectory(point)) {
				//If sending the point doesn't work, log an error and exit.
				Logger.addEvent("Buffer full!", CANTalonMPLoader.class);
				break;
			}
		}
	}
}
