package org.usfirst.frc.team449.robot.util;

import com.ctre.CANTalon;

/**
 * Utility class for running motion profiles on a CANTalon SRX.
 */
public class CANTalonMPRunner {

	/**
	 * The CANTalons to run motion profiles on.
	 */
	private CANTalon[] talons;

	/**
	 * The minimum number of points that must be in the bottom MP buffer to start a profile.
	 */
	private int minNumPointsInBtmBuffer;

	/**
	 * Default constructor.
	 * @param talons The CANTalons to run motion profiles on.
	 * @param minNumPointsInBtmBuffer The minimum number of points that must be in the bottom MP buffer to start a profile.
	 */
	public CANTalonMPRunner(CANTalon[] talons, int minNumPointsInBtmBuffer){
		this.talons = talons;
		this.minNumPointsInBtmBuffer = minNumPointsInBtmBuffer;
	}

	/**
	 * Get whether the talons are ready to start running the loaded profile.
	 * @return true if all the talons either have at least minNumPointsInBtmBuffer points in the bottom buffer or no
	 * points remaining in the API-level buffer, false otherwise.
	 */
	public boolean isReady(){
		return isReady(talons, minNumPointsInBtmBuffer);
	}

	/**
	 * Get whether the talons are done running a motion profile.
	 * @return true if the all the talons have the final point as the active one, false otherwise.
	 */
	public boolean isFinished(){
		return isFinished(talons);
	}

	/**
	 * Start running the loaded profile on the given talons.
	 */
	public void startRunningProfile(){
		startRunningProfile(talons);
	}

	/**
	 * Disable the talons.
	 */
	public void disableTalons(){
		disableTalons(talons);
	}

	/**
	 * Have the talons hold their current position via closed-loop control.
	 */
	public void holdTalons(){
		holdTalons(talons);
	}

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

	/**
	 * Disable the given talons.
	 * @param talons The talons to disable.
	 */
	public static void disableTalons(CANTalon[] talons){
		for (CANTalon talon : talons){
			talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
			talon.set(CANTalon.SetValueMotionProfile.Disable.value);
		}
	}

	/**
	 * Have the given talons hold their current position via closed-loop control.
	 * @param talons The talons to have hold their position.
	 */
	public static void holdTalons(CANTalon[] talons){
		for (CANTalon talon : talons){
			talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
			talon.set(CANTalon.SetValueMotionProfile.Hold.value);
		}
	}
}
