package org.usfirst.frc.team449.robot.util;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Notifier;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;

/**
 * Utility class for loading points into a CANTalon's motion profile buffer.
 */
public class CANTalonMPLoader {

	/**
	 * The talons to load profiles into.
	 */
	private RotPerSecCANTalonSRX[] talons;

	/**
	 * The notifier for the thread in which points are moved from the API-level buffer to the low-level one.
	 */
	private Notifier MPNotifier;

	/**
	 * The period of the thread in which points are moved from the API-level buffer to the low-level one, in seconds.
	 */
	private double updaterProcessPeriodSecs;

	/**
	 * Default constructor.
	 * @param talons The talons to load profiles into.
	 * @param updaterProcessPeriodSecs The period of the thread in which points are moved from the API-level buffer to
	 *                                    the low-level one, in seconds.
	 */
	public CANTalonMPLoader(RotPerSecCANTalonSRX[] talons, double updaterProcessPeriodSecs){
		this.talons = talons;
		this.updaterProcessPeriodSecs = updaterProcessPeriodSecs;
		//Set up the updater
		CANTalonMPUpdaterProcess updaterProcess = new CANTalonMPUpdaterProcess();
		for (RotPerSecCANTalonSRX talon : this.talons) {
			updaterProcess.addTalon(talon.canTalon);
		}
		//Set up the notifier.
		MPNotifier = new Notifier(updaterProcess);
	}

	/**
	 * Loads the given motion profile into the Talon's API-level buffer.
	 * @param data The profile to load.
	 */
	public void loadTopLevel(MotionProfileData data){
		//Stop the updater while we load the API-level buffer
		startUpdaterProcess();
		//Load the profile into each talon.
		for (RotPerSecCANTalonSRX talon : talons){
			loadTopLevel(data, talon);
		}
		//Resume the updater.
		startUpdaterProcess();
	}

	/**
	 * Start running the thread that loads points from the API-level buffer into the low-level one.
	 */
	public void startUpdaterProcess(){
		MPNotifier.startPeriodic(updaterProcessPeriodSecs);
	}

	/**
	 * Stop running the thread that loads points from the API-level buffer into the low-level one.
	 */
	public void stopUpdaterProcess(){
		MPNotifier.stop();
	}

	/**
	 * Load in one profile to each Talon.
	 * @param profiles An array of profiles at least as long as the list of Talons this object was constructed with.
	 */
	public void loadIndividualProfiles(MotionProfileData[] profiles){
		//Stop the updater while we load the API-level buffer
		stopUpdaterProcess();
		//Load each profile
		for (int i = 0; i < talons.length; i++){
			loadTopLevel(profiles[i], talons[i]);
		}
		//Resume the updater.
		startUpdaterProcess();
	}

	/**
	 * Loads the given motion profile into the given Talon's API-level buffer.
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
