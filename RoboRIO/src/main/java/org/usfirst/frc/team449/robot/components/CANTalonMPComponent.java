package org.usfirst.frc.team449.robot.components;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.jacksonWrappers.RPSTalon;
import org.usfirst.frc.team449.robot.other.MotionProfileData;

/**
 * Component class for loading and running profiles on a {@link RPSTalon}.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CANTalonMPComponent {

	/**
	 * The talons with the RPSTalon wrapper to convert from feet to native units.
	 */
	@NotNull
	private final RPSTalon[] RPSTalons;

	/**
	 * Default constructor.
	 *
	 * @param talons The talons to load profiles into.
	 */
	@JsonCreator
	public CANTalonMPComponent(@NotNull @JsonProperty(required = true) RPSTalon[] talons) {
		this.RPSTalons = talons;
	}

	/**
	 * Get whether the given talons are ready to start running the loaded profile.
	 *
	 * @param talons The talons the profiles are on.
	 * @return true if all the talons say they're ready..
	 */
	public static boolean isReady(RPSTalon[] talons) {
		//Assume true, set to false if even one talon doesn't meet the criteria
		boolean ready = true;
		for (RPSTalon talon : talons) {
			ready = ready && talon.readyForMP();
		}
		return ready;
	}

	/**
	 * Get whether the given talons are done running a motion profile.
	 *
	 * @param talons The talons the profiles are on.
	 * @return true if the all the talons are finished, false otherwise.
	 */
	public static boolean isFinished(RPSTalon[] talons) {
		//Assume true, set to false if even one talon doesn't meet the criteria
		boolean finished = true;
		for (RPSTalon talon : talons) {
			finished = finished && talon.MPIsFinished();
		}
		return finished;
	}

	/**
	 * Start running the loaded profile on the given talons.
	 *
	 * @param talons The talons the profiles are on.
	 */
	public static void startRunningProfile(RPSTalon[] talons) {
		//Set up the talons to run
		for (RPSTalon talon : talons) {
			talon.clearMPUnderrun();
		}
		//Actually start running them in another loop so they start as simultaneously as possible.
		for (RPSTalon talon : talons) {
			talon.startRunningMP();
		}
	}

	/**
	 * Have the given talons hold their current position via closed-loop control.
	 *
	 * @param talons The talons to have hold their position.
	 */
	public static void holdTalons(RPSTalon[] talons) {
		for (RPSTalon talon : talons) {
			talon.holdPositionMP();
		}
	}


	/**
	 * Loads the given motion profile into the Talon's API-level buffer.
	 *
	 * @param data The profile to load.
	 */
	public void loadTopLevel(MotionProfileData data) {
		//Load the profile into each talon.
		for (RPSTalon talon : RPSTalons) {
			talon.loadProfile(data);
		}
	}

	/**
	 * Load in one profile to each Talon.
	 *
	 * @param profiles An array of profiles at least as long as the list of Talons this object was constructed with.
	 */
	public void loadIndividualProfiles(MotionProfileData[] profiles) {
		for (int i = 0; i < RPSTalons.length; i++) {
			RPSTalons[i].loadProfile(profiles[i]);
		}
	}

	/**
	 * Get whether the talons are ready to start running the loaded profile.
	 *
	 * @return true if all the talons are ready.
	 */
	public boolean isReady() {
		return isReady(RPSTalons);
	}

	/**
	 * Get whether the talons are done running a motion profile.
	 *
	 * @return true if the all the talons are finished.
	 */
	public boolean isFinished() {
		return isFinished(RPSTalons);
	}

	/**
	 * Start running the loaded profile on the given talons.
	 */
	public void startRunningProfile() {
		startRunningProfile(RPSTalons);
	}

	/**
	 * Have the talons hold their current position.
	 */
	public void holdTalons() {
		holdTalons(RPSTalons);
	}
}
