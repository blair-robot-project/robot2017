package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.BinaryMotorSubsystem;
import org.usfirst.frc.team449.robot.util.Loggable;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Class for the flywheel
 */
public class SingleFlywheelShooter extends MappedSubsystem implements Loggable, BinaryMotorSubsystem{
	/**
	 * The flywheel's Talon
	 */
	public RotPerSecCANTalonSRX talon;

	/**
	 * Whether the flywheel is currently commanded to spin
	 */
	private boolean spinning;

	/**
	 * Throttle at which to run the shooter, defaults to 0.5
	 */
	private double throttle;

	/**
	 * Construct a SingleFlywheelShooter
	 *
	 * @param map config map
	 */
	public SingleFlywheelShooter(maps.org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter
			                             .SingleFlywheelShooterMap.SingleFlywheelShooter map) {
		super(map.getMechanism());
		this.map = map;
		this.talon = new RotPerSecCANTalonSRX(map.getTalon());

		this.throttle = map.getThrottle();
		Logger.addEvent("Shooter F: " + talon.canTalon.getF(), this.getClass());
	}

	/**
	 * Set the flywheel's percent voltage
	 *
	 * @param sp percent voltage setpoint [-1, 1]
	 */
	private void setVBusSpeed(double sp) {
		talon.setPercentVbus(sp);
	}

	/**
	 * Set the flywheel's percent PID velocity setpoint
	 *
	 * @param sp percent PID velocity setpoint [-1, 1]
	 */
	private void setPIDSpeed(double sp) {
		talon.setSpeed(talon.getMaxSpeed() * sp);
	}

	/**
	 * A wrapper around the speed method we're currently using/testing
	 *
	 * @param sp The speed to go at [-1, 1]
	 */
	private void setDefaultSpeed(double sp) {
		setPIDSpeed(sp);
	}

	/**
	 * Init the log file on enabling
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	@Override
	public String getHeader() {
		return "speed,setpoint,error,voltage,current";
	}

	@Override
	public Object[] getData() {
		return new Object[]{talon.getSpeed(),talon.getSetpoint(),talon.getError(),talon.canTalon.getOutputVoltage(),talon.canTalon.getOutputCurrent()};
	}

	@Override
	public String getName(){
		return "shooter";
	}

	/**
	 * Turns the motor on, and sets it to a map-specified speed.
	 */
	@Override
	public void turnMotorOn() {
		talon.canTalon.enable();
		setDefaultSpeed(throttle);
		spinning = true;
	}

	/**
	 * Turns the motor off.
	 */
	@Override
	public void turnMotorOff() {
		setDefaultSpeed(0);
		talon.canTalon.disable();
		spinning = false;
	}

	/**
	 * Get the current state of the motor.
	 *
	 * @return true if the motor is on, false otherwise.
	 */
	@Override
	public boolean isMotorOn() {
		return spinning;
	}
}
