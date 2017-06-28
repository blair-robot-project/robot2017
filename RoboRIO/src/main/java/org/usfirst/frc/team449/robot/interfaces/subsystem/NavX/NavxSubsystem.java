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
	@NotNull
	AHRS getNavX();
}
