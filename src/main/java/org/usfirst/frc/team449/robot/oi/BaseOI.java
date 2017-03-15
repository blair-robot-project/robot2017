package org.usfirst.frc.team449.robot.oi;

/**
 * A basic OI class that all OI classes should implement.
 */
public abstract class BaseOI {
	/**
	 * Map all buttons to commands. Should only be run after all subsystems have been instantiated.
	 */
	public abstract void mapButtons();
}
