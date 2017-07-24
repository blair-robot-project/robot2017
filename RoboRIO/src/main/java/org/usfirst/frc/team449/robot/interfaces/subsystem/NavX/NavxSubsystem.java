package org.usfirst.frc.team449.robot.interfaces.subsystem.NavX;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kauailabs.navx.frc.AHRS;
import org.jetbrains.annotations.NotNull;

/**
 * A subsystem that has a NavX on it.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT)
public interface NavxSubsystem {

	/**
	 * Get the robot's heading using the navX
	 *
	 * @return robot heading, in degrees, on [-180, 180]
	 */
	double getGyroOutput();

	/**
	 * @return true if the NavX is currently overriden, false otherwise.
	 */
	boolean getOverrideNavX();

	/**
	 * @param override true to override the NavX, false to un-override it.
	 */
	void setOverrideNavX(boolean override);

	/**
	 * @return An AHRS object representing this subsystem's NavX.
	 */
	@NotNull
	AHRS getNavX();
}
