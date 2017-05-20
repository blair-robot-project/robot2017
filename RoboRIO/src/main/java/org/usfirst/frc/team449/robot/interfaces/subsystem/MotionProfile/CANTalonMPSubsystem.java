package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile;

import com.ctre.CANTalon;

import java.util.List;

/**
 * Created by noah on 5/20/17.
 */
public interface CANTalonMPSubsystem {
	/**
	 * Loads the profile with the given name into the MP buffer.
	 * @param name The name of the profile.
	 */
	void loadMotionProfile(String name);

	/**
	 * Get the Talons in this subsystem to run the MP on.
	 * @return a List of Talons with encoders attached (e.g. master talons)
	 */
	List<CANTalon> getTalons();

	/**
	 * Get the minimum number of points that can be in the bottom-level motion profile buffer before we start driving the profile
	 * @return an integer from [0, 128]
	 */
	int getMinPointsInBtmBuffer();

	/**
	 * Stops any MP-related threads currently running. Normally called at the start of teleop.
	 */
	void stopMPProcesses();
}
