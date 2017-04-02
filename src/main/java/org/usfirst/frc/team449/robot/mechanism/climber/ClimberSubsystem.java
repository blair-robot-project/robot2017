package org.usfirst.frc.team449.robot.mechanism.climber;

import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;

/**
 * The climber, with power monitoring to stop.
 */
public class ClimberSubsystem extends MechanismSubsystem {
	/**
	 * The CANTalon controlling the climber.
	 */
	public RotPerSecCANTalonSRX canTalonSRX;

	/**
	 * The maximum allowable power before we stop the motor.
	 */
	private double max_power;
	private VictorSP victor;

	/**
	 * Construct a ClimberSubsystem
	 * @param map the config map
	 */
	public ClimberSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.climber.ClimberMap.Climber map) {
		super(map.getMechanism());
		//Instantiate things
		this.map = map;
		canTalonSRX = new RotPerSecCANTalonSRX(map.getWinch());
		this.max_power = map.getMaxPower();
		if (map.hasVictor()) {
			this.victor = new VictorSP(map.getVictor().getPort());
			victor.setInverted(map.getVictor().getInverted());
		}
	}

	/**
	 * Initialize the default command for a subsystem By default subsystems have
	 * no default command, but if they do, the default command is set with this
	 * method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
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
		if (victor != null) {
			victor.set(percentVbus);
		}
	}

	/**
	 * Whether or not the power limit has been reached.
	 * @return If the output power is greater than the max allowable power.
	 */
	public boolean reachedTop() {
		return Math.abs(canTalonSRX.getPower()) > max_power;
	}
}