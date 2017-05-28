package org.usfirst.frc.team449.robot.mechanism.climber;

import edu.wpi.first.wpilibj.VictorSP;
import org.usfirst.frc.team449.robot.components.MappedVictor;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.interfaces.subsystem.binaryMotor.BinaryMotorSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.conditional.ConditionalSubsystem;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;
import org.usfirst.frc.team449.robot.util.BufferTimer;
import org.usfirst.frc.team449.robot.util.Loggable;

/**
 * A climber subsystem that uses power monitoring to stop climbing.
 */
public class ClimberSubsystem extends MechanismSubsystem implements Loggable, BinaryMotorSubsystem, ConditionalSubsystem {
	/**
	 * The CANTalon controlling one of the climber motors.
	 */
	private RotPerSecCANTalonSRX canTalonSRX;

	/**
	 * The Victor controlling the other climber motor.
	 */
	private VictorSP victor;

	/**
	 * The maximum allowable power before we stop the motor.
	 */
	private double max_power;

	/**
	 * The bufferTimer controlling how long we can be above the current limit before we stop climbing.
	 */
	private BufferTimer currentLimitTimer;

	/**
	 * Whether or not the motor is currently spinning.
	 */
	private boolean motorSpinning;

	/**
	 * Construct a ClimberSubsystem
	 *
	 * @param map the config map
	 */
	public ClimberSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.climber.ClimberMap.Climber map) {
		super(map.getMechanism());
		//Instantiate things
		this.map = map;
		canTalonSRX = new RotPerSecCANTalonSRX(map.getWinch());
		this.max_power = map.getMaxPower();
		currentLimitTimer = new BufferTimer(map.getMillisAboveMaxPower());
		motorSpinning = false;

		//Victor is optional
		if (map.hasVictor()) {
			this.victor = new MappedVictor(map.getVictor());
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
	 *
	 * @param percentVbus The voltage to give the motor, from -1 to 1.
	 */
	private void setPercentVbus(double percentVbus) {
		canTalonSRX.setPercentVbus(percentVbus);
		if (victor != null) {
			victor.set(percentVbus);
		}
	}

	/**
	 * Get the headers for the data this subsystem logs every loop.
	 * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
	 */
	@Override
	public String[] getHeader() {
		return new String[]{"current,",
				"voltage,",
				"power"};
	}

	/**
	 * Get the data this subsystem logs every loop.
	 * @return An N-length array of Objects, where N is the number of labels given by getHeader.
	 */
	@Override
	public Object[] getData() {
		return new Object[]{canTalonSRX.canTalon.getOutputCurrent(),
				canTalonSRX.canTalon.getOutputVoltage(),
				canTalonSRX.getPower()};
	}

	/**
	 * Get the name of this object.
	 * @return A string that will identify this object in the log file.
	 */
	@Override
	public String getName() {
		return "climber";
	}

	/**
	 * Turns the motor on, and sets it to a map-specified speed.
	 */
	@Override
	public void turnMotorOn() {
		canTalonSRX.canTalon.enable();
		setPercentVbus(1);
		motorSpinning = true;
	}

	/**
	 * Turns the motor off.
	 */
	@Override
	public void turnMotorOff() {
		setPercentVbus(0);
		canTalonSRX.canTalon.disable();
		motorSpinning = false;
	}

	/**
	 * Get the current state of the motor.
	 *
	 * @return true if the motor is on, false otherwise.
	 */
	@Override
	public boolean isMotorOn() {
		return motorSpinning;
	}

	/**
	 * Whether or not the current limit has been exceeded
	 *
	 * @return true if exceeded, false otherwise
	 */
	@Override
	public boolean isConditionTrue() {
		return currentLimitTimer.get(Math.abs(canTalonSRX.getPower()) > max_power);
	}
}