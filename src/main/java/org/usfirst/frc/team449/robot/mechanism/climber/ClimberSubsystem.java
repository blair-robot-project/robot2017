package org.usfirst.frc.team449.robot.mechanism.climber;

import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;

/**
 * The climber, with current monitoring to stop.
 */
public class ClimberSubsystem extends MechanismSubsystem {
	/**
	 * The CANTalon controlling the climber.
	 */
	public RotPerSecCANTalonSRX canTalonSRX;

	/**
	 * The maximum allowable current before we stop the motor.
	 */
	private double max_current;

	public ClimberSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.climber.ClimberMap.Climber map) {
		super(map.getMechanism());
		//Instantiate things
		this.map = map;
		canTalonSRX = new RotPerSecCANTalonSRX(map.getWinch());
		this.max_current = map.getMaxCurrent();
	}

	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	/**
	 * Set the percent voltage to be given to the motor.
	 * @param percentVbus The voltage to give the motor, from -1 to 1.
	 */
	public void setPercentVbus(double percentVbus) {
		canTalonSRX.setPercentVbus(percentVbus);
	}

	/**
	 * Whether or not the current limit has been reached.
	 * @return If the output current is greater than the max allowable current.
	 */
	public boolean reachedTop() {
		return canTalonSRX.canTalon.getOutputCurrent() > max_current;
	}
}