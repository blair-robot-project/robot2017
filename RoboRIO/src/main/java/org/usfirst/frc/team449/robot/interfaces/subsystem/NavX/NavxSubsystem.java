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

	void setOverrideNavX(boolean override);

	boolean getOverrideNavX();

	AHRS getNavX();
}
