package org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor;

/**
 * A subsystem with a motor that only needs to be run at one speed, e.g. a flywheel shooter or simple intake.
 */
public interface BinaryMotorSubsystem {

	/**
	 * Turns the motor on, and sets it to a map-specified speed.
	 */
	void turnMotorOn();

	/**
	 * Turns the motor off.
	 */
	void turnMotorOff();

	/**
	 * Get the current state of the motor.
	 * @return true if the motor is on, false otherwise.
	 */
	boolean isMotorOn();
}
