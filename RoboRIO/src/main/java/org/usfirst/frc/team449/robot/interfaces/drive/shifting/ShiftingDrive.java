package org.usfirst.frc.team449.robot.interfaces.drive.shifting;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;

/**
 * A drive that has a high gear and a low gear and can switch between them.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface ShiftingDrive {

	/**
	 * @return The gear this subsystem is currently in.
	 */
	@NotNull
	gear getGear();

	/**
	 * Shift to a specific gear.
	 *
	 * @param gear Which gear to shift to.
	 */
	void setGear(@NotNull gear gear);

	/**
	 * @return true if currently overriding autoshifting, false otherwise.
	 */
	boolean getOverrideAutoshift();

	/**
	 * @param override Whether or not to override autoshifting.
	 */
	void setOverrideAutoshift(boolean override);

	enum gear {
		HIGH, LOW
	}
}
