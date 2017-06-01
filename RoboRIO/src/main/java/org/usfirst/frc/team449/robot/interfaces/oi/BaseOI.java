package org.usfirst.frc.team449.robot.interfaces.oi;

/**
 * A basic OI interface that all OI classes should implement.
 */
public interface BaseOI {
	/**
	 * Map all buttons to commands. Should only be run after all subsystems have been instantiated.
	 */
	void mapButtons();
}
