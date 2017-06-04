package org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * A subsystem with a single DoubleSolenoid piston.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_OBJECT, property="@class")
public interface SolenoidSubsystem {
	/**
	 * Set the solenoid to a certain position.
	 *
	 * @param value Forward to extend the Solenoid, Reverse to contract it.
	 */
	void setSolenoid(DoubleSolenoid.Value value);

	/**
	 * Get the position of the solenoid.
	 *
	 * @return Forward if extended, Reverse if contracted.
	 */
	DoubleSolenoid.Value getSolenoidPosition();
}
