package org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 * Created by noah on 5/13/17.
 */
public interface SolenoidSubsystem {
	/**
	 * Set the solenoid to a certain position.
	 * @param value Forward to extend the Solenoid, Reverse to contract it.
	 */
	void setSolenoid(DoubleSolenoid.Value value);

	/**
	 * Get the position of the solenoid.
	 * @return Forward if extended, Reverse if contracted.
	 */
	DoubleSolenoid.Value getSolenoidPosition();
}
