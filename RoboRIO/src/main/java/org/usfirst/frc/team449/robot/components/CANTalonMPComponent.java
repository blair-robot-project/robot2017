package org.usfirst.frc.team449.robot.components;

import com.ctre.CANTalon;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.Notifier;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.RotPerSecCANTalon;
import org.usfirst.frc.team449.robot.logger.Logger;
import org.usfirst.frc.team449.robot.other.MotionProfileData;

/**
 * Component class for loading and running profiles on a {@link CANTalon}.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CANTalonMPComponent {

	/**
	 * The talons with the RPSCANTalonSRX wrapper to convert from feet to native units.
	 */
	@NotNull
	private final RotPerSecCANTalon[] RPSTalons;

	/**
	 * The talons without any wrapper.
	 */
	@NotNull
	private final CANTalon[] talons;

	/**
	 * The notifier for the thread in which points are moved from the API-level buffer to the low-level one.
	 */
	@NotNull
	private final Notifier MPNotifier;

	/**
	 * The period of the thread in which points are moved from the API-level buffer to the low-level one, in seconds.
	 */
	private final double updaterProcessPeriodSecs;

	/**
	 * The minimum number of points that must be in the bottom MP buffer to start a profile.
	 */
	private final int minNumPointsInBtmBuffer;

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
	public CANTalonMPComponent(@NotNull @JsonProperty(required = true) RotPerSecCANTalon[] talons,
	                           @JsonProperty(required = true) double updaterProcessPeriodSecs,
	                           @JsonProperty(required = true) int minNumPointsInBtmBuffer) {
		this.RPSTalons = talons;
		this.updaterProcessPeriodSecs = updaterProcessPeriodSecs;
		this.minNumPointsInBtmBuffer = minNumPointsInBtmBuffer;
		//Instantiate the CANTalon list
		this.talons = new CANTalon[RPSTalons.length];
		//Set up the talon list
		for (int i = 0; i < this.RPSTalons.length; i++) {
			//Make a list of the inner talon class.
			this.talons[i] = this.RPSTalons[i].getCanTalon();
		}
		//Set up the notifier.
		MPNotifier = new Notifier(this::processMPBuffer);
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
			talon.set(CANTalon.SetValueMotionProfile.Enable.value);
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
	public static void loadTopLevel(MotionProfileData data, RotPerSecCANTalon talon) {
		//Clear all the MP-related stuff on talon
		talon.getCanTalon().disable();
		talon.getCanTalon().clearMotionProfileHasUnderrun();
		talon.getCanTalon().clearMotionProfileTrajectories();


		for (int i = 0; i < data.getData().length; ++i) {
			CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();
			//Set parameters that are true for all points
			point.profileSlotSelect = 1;    // gain selection
			point.velocityOnly = false;  // true => no position servo just velocity feedforward
			// Set all the fields of the profile point
			point.position = talon.feetToNative(data.getData()[i][0]);
			point.velocity = talon.feetPerSecToNative(data.getData()[i][1]);
			point.timeDurMs = (int) (data.getData()[i][2] * 1000.);
			point.zeroPos = i == 0; // If its the first point, set the encoder position to 0.
			point.isLastPoint = (i + 1) == data.getData().length; // If its the last point, isLastPoint = true

			// Send the point to the Talon's buffer
			if (!talon.getCanTalon().pushMotionProfileTrajectory(point)) {
				//If sending the point doesn't work, log an error and exit.
				Logger.addEvent("Buffer full!", CANTalonMPComponent.class);
				System.out.println("Buffer full!");
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
		stopUpdaterProcess();
		//Load the profile into each talon.
		for (RotPerSecCANTalon talon : RPSTalons) {
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
		for (int i = 0; i < RPSTalons.length; i++) {
			loadTopLevel(profiles[i], RPSTalons[i]);
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

	/**
	 * For each talon, move points from the API-level MP buffer to the bottom one.
	 */
	private void processMPBuffer() {
		for (CANTalon talon : talons) {
			talon.processMotionProfileBuffer();
		}
	}
}
