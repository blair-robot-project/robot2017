package org.usfirst.frc.team449.robot.util;

import com.ctre.CANTalon;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Notifier;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;

/**
 * Utility class for loading and running profiles on a {@link CANTalon}.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class CANTalonMPHandler {

	/**
	 * The talons with the RPSCANTalonSRX wrapper to convert from feet to native units.
	 */
	private RotPerSecCANTalonSRX[] RPStalons;

	/**
	 * The talons without any wrapper.
	 */
	private CANTalon[] talons;

	/**
	 * The notifier for the thread in which points are moved from the API-level buffer to the low-level one.
	 */
	private Notifier MPNotifier;

	/**
	 * The period of the thread in which points are moved from the API-level buffer to the low-level one, in seconds.
	 */
	private double updaterProcessPeriodSecs;

	/**
	 * The minimum number of points that must be in the bottom MP buffer to start a profile.
	 */
	private int minNumPointsInBtmBuffer;

	/**
	 * Default constructor.
	 *
	 * @param talons                   The talons to load profiles into.
	 * @param updaterProcessPeriodSecs The period of the thread in which points are moved from the API-level buffer to
	 *                                 the low-level one, in seconds.
	 * @param minNumPointsInBtmBuffer  The minimum number of points that must be in the bottom MP buffer to start a
	 *                                 profile.
	 */
	@JsonCreator
	public CANTalonMPHandler(@JsonProperty(required = true) RotPerSecCANTalonSRX[] talons,
	                         @JsonProperty(required = true) double updaterProcessPeriodSecs,
	                         @JsonProperty(required = true) int minNumPointsInBtmBuffer) {
		this.RPStalons = talons;
		this.updaterProcessPeriodSecs = updaterProcessPeriodSecs;
		this.minNumPointsInBtmBuffer = minNumPointsInBtmBuffer;
		//Instantiate the CANTalon list
		this.talons = new CANTalon[RPStalons.length];
		//Set up the updater
		CANTalonMPUpdaterProcess updaterProcess = new CANTalonMPUpdaterProcess();
		for (int i = 0; i < this.RPStalons.length; i++) {
			//Add the talon to the updater
			updaterProcess.addTalon(this.RPStalons[i].canTalon);
			//Make a list of the inner talon class.
			this.talons[i] = this.RPStalons[i].canTalon;
		}
		//Set up the notifier.
		MPNotifier = new Notifier(updaterProcess);
	}

	/**
	 * Get whether the given talons are ready to start running the loaded profile.
	 *
	 * @param talons                  The talons the profiles are on.
	 * @param minNumPointsInBtmBuffer The minimum number of points that must be in the bottom MP buffer to start the
	 *                                profile.
	 * @return true if all the talons either have at least minNumPointsInBtmBuffer points in the bottom buffer or no
	 * points remaining in the API-level buffer, false otherwise.
	 */
	public static boolean isReady(CANTalon[] talons, int minNumPointsInBtmBuffer) {
		//Assume true, set to false if even one talon doesn't meet the criteria
		boolean ready = true;
		for (CANTalon talon : talons) {
			CANTalon.MotionProfileStatus status = new CANTalon.MotionProfileStatus();
			talon.getMotionProfileStatus(status);
			//Set to false if the top buffer still has points and the bottom buffer isn't full enough.
			ready = ready && (status.topBufferCnt == 0 || status.btmBufferCnt >= minNumPointsInBtmBuffer);
		}
		return ready;
	}

	/**
	 * Get whether the given talons are done running a motion profile.
	 *
	 * @param talons The talons the profiles are on.
	 * @return true if the all the talons have the final point as the active one, false otherwise.
	 */
	public static boolean isFinished(CANTalon[] talons) {
		//Assume true, set to false if even one talon doesn't meet the criteria
		boolean finished = true;
		for (CANTalon talon : talons) {
			CANTalon.MotionProfileStatus status = new CANTalon.MotionProfileStatus();
			talon.getMotionProfileStatus(status);
			//We check if the current point is the final one to see if the profile is finished.
			finished = finished && status.activePoint.isLastPoint;
		}
		return finished;
	}

	/**
	 * Start running the loaded profile on the given talons.
	 *
	 * @param talons The talons the profiles are on.
	 */
	public static void startRunningProfile(CANTalon[] talons) {
		//Set up the talons to run
		for (CANTalon talon : talons) {
			talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
			talon.clearMotionProfileHasUnderrun();
			talon.enable();
		}
		//Actually start running them in another loop so they start as simultaneously as possible.
		for (CANTalon talon : talons) {
			talon.setControlMode(CANTalon.SetValueMotionProfile.Enable.value);
		}
	}

	/**
	 * Disable the given talons.
	 *
	 * @param talons The talons to disable.
	 */
	public static void disableTalons(CANTalon[] talons) {
		for (CANTalon talon : talons) {
			talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
			talon.set(CANTalon.SetValueMotionProfile.Disable.value);
		}
	}

	/**
	 * Have the given talons hold their current position via closed-loop control.
	 *
	 * @param talons The talons to have hold their position.
	 */
	public static void holdTalons(CANTalon[] talons) {
		for (CANTalon talon : talons) {
			talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
			talon.set(CANTalon.SetValueMotionProfile.Hold.value);
		}
	}

	/**
	 * Loads the given motion profile into the given Talon's API-level buffer.
	 *
	 * @param data  The profile to load.
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
				Logger.addEvent("Buffer full!", CANTalonMPHandler.class);
				break;
			}
		}
	}

	/**
	 * Loads the given motion profile into the Talon's API-level buffer.
	 *
	 * @param data The profile to load.
	 */
	public void loadTopLevel(MotionProfileData data) {
		//Stop the updater while we load the API-level buffer
		startUpdaterProcess();
		//Load the profile into each talon.
		for (RotPerSecCANTalonSRX talon : RPStalons) {
			loadTopLevel(data, talon);
		}
		//Resume the updater.
		startUpdaterProcess();
	}

	/**
	 * Start running the thread that loads points from the API-level buffer into the low-level one.
	 */
	public void startUpdaterProcess() {
		MPNotifier.startPeriodic(updaterProcessPeriodSecs);
	}

	/**
	 * Stop running the thread that loads points from the API-level buffer into the low-level one.
	 */
	public void stopUpdaterProcess() {
		MPNotifier.stop();
	}

	/**
	 * Load in one profile to each Talon.
	 *
	 * @param profiles An array of profiles at least as long as the list of Talons this object was constructed with.
	 */
	public void loadIndividualProfiles(MotionProfileData[] profiles) {
		//Stop the updater while we load the API-level buffer
		stopUpdaterProcess();
		//Load each profile
		for (int i = 0; i < RPStalons.length; i++) {
			loadTopLevel(profiles[i], RPStalons[i]);
		}
		//Resume the updater.
		startUpdaterProcess();
	}

	/**
	 * Get whether the talons are ready to start running the loaded profile.
	 *
	 * @return true if all the talons either have at least minNumPointsInBtmBuffer points in the bottom buffer or no
	 * points remaining in the API-level buffer, false otherwise.
	 */
	public boolean isReady() {
		return isReady(talons, minNumPointsInBtmBuffer);
	}

	/**
	 * Get whether the talons are done running a motion profile.
	 *
	 * @return true if the all the talons have the final point as the active one, false otherwise.
	 */
	public boolean isFinished() {
		return isFinished(talons);
	}

	/**
	 * Start running the loaded profile on the given talons.
	 */
	public void startRunningProfile() {
		startRunningProfile(talons);
	}

	/**
	 * Disable the talons.
	 */
	public void disableTalons() {
		disableTalons(talons);
	}

	/**
	 * Have the talons hold their current position via closed-loop control.
	 */
	public void holdTalons() {
		holdTalons(talons);
	}
}
