package org.usfirst.frc.team449.robot.generalInterfaces.shiftable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for any object that different settings for different gears
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface Shiftable {

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

	enum gear {
		HIGH, LOW
	}

}
