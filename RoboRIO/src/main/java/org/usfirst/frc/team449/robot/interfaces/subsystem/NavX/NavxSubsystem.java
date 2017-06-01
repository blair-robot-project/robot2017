package org.usfirst.frc.team449.robot.interfaces.subsystem.NavX;


import com.kauailabs.navx.frc.AHRS;

/**
 * A subsystem that has a NavX on it.
 */
public interface NavxSubsystem {
	/**
	 * Get the output of the NavX
	 *
	 * @return The heading, on a scale of -180 to 180.
	 */
	double getGyroOutput();

	/**
	 * Get whether this subsystem's NavX is currently being overriden.
	 *
	 * @return true if the NavX is overriden, false otherwise.
	 */
	boolean getOverrideNavX();

	/**
	 * Set whether or not to override this subsystem's NavX.
	 *
	 * @param override true to override, false otherwise.
	 */
	void setOverrideNavX(boolean override);

	/**
	 * Get the NavX this subsystem uses.
	 *
	 * @return An AHRS object representing this subsystem's NavX.
	 */
	AHRS getNavX();
}
