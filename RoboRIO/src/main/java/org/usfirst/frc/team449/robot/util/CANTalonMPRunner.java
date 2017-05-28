package org.usfirst.frc.team449.robot.util;

import com.ctre.CANTalon;

/**
 * Utility class for running motion profiles on a CANTalon SRX.
 */
public class CANTalonMPRunner {

	/**
	 * Get whether the given talons are ready to start running the loaded profile.
	 * @param talons The talons the profiles are on.
	 * @param minNumPointsInBtmBuffer The minimum number of points that must be in the bottom MP buffer to start the profile.
	 * @return true if all the talons either have at least minNumPointsInBtmBuffer points in the bottom buffer or no
	 * points remaining in the API-level buffer, false otherwise.
	 */
	public static boolean isReady(CANTalon[] talons, int minNumPointsInBtmBuffer){
		//Assume true, set to false if even one talon doesn't meet the criteria
		boolean ready = true;
		for(CANTalon talon : talons){
			CANTalon.MotionProfileStatus status = new CANTalon.MotionProfileStatus();
			talon.getMotionProfileStatus(status);
			//Set to false if the top buffer still has points and the bottom buffer isn't full enough.
			ready = ready && (status.topBufferCnt == 0 || status.btmBufferCnt >= minNumPointsInBtmBuffer);
		}
		return ready;
	}

	/**
	 * Get whether the given talons are done running a motion profile.
	 * @param talons The talons the profiles are on.
	 * @return true if the all the talons have the final point as the active one, false otherwise.
	 */
	public static boolean isFinished(CANTalon[] talons){
		//Assume true, set to false if even one talon doesn't meet the criteria
		boolean finished = true;
		for (CANTalon talon : talons){
			CANTalon.MotionProfileStatus status = new CANTalon.MotionProfileStatus();
			talon.getMotionProfileStatus(status);
			//We check if the current point is the final one to see if the profile is finished.
			finished = finished && status.activePoint.isLastPoint;
		}
		return finished;
	}

	/**
	 * Start running the loaded profile on the given talons.
	 * @param talons The talons the profiles are on.
	 */
	public static void startRunningProfile(CANTalon[] talons){
		//Set up the talons to run
		for (CANTalon talon : talons){
			talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
			talon.clearMotionProfileHasUnderrun();
			talon.enable();
		}
		//Actually start running them in another loop so they start as simultaneously as possible.
		for (CANTalon talon : talons){
			talon.setControlMode(CANTalon.SetValueMotionProfile.Enable.value);
		}
	}
}
