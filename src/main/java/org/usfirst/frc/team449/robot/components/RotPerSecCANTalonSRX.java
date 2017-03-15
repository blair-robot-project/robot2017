package org.usfirst.frc.team449.robot.components;

import com.ctre.CANTalon;
import maps.org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRXMap;

/**
 * Component wrapper on CTRE CAN Talon SRX {@link CANTalon}, with unit conversions to/from RPS built in. Every
 * method in this class takes arguments in RPS (except nativeToRPS of course).
 */
public class RotPerSecCANTalonSRX extends Component {

	/**
	 * The CTRE CAN Talon SRX that this class is a wrapper on
	 */
	public CANTalon canTalon;

	/**
	 * The maximum speed of the motor, in RPS.
	 */
	protected double maxSpeed;

	/**
	 * The counts per rotation of the encoder being used.
	 */
	public double encoderCPR;

	/**
	 * The type of encoder the talon uses.
	 */
	protected CANTalon.FeedbackDevice feedbackDevice;

	/**
	 * 	The map used to construct this object.
	 */
	private RotPerSecCANTalonSRXMap.RotPerSecCANTalonSRX map;

	/**
	 * Construct the CANTalonSRX from its map object
	 *
	 * @param map CANTalonSRX map object
	 */
	public RotPerSecCANTalonSRX(RotPerSecCANTalonSRXMap.RotPerSecCANTalonSRX map) {
		// Configure stuff
		this.map = map;
		canTalon = new CANTalon(map.getPort());
		encoderCPR = map.getEncoderCPR();
		canTalon.reverseSensor(map.getReverseSensor());
		canTalon.reverseOutput(map.getReverseOutput());
		canTalon.setInverted(map.getIsInverted());
		canTalon.ConfigFwdLimitSwitchNormallyOpen(map.getFwdLimNormOpen());
		canTalon.ConfigRevLimitSwitchNormallyOpen(map.getRevLimNormOpen());
		canTalon.enableLimitSwitch(map.getFwdLimEnabled(), map.getRevLimEnabled());
		canTalon.enableForwardSoftLimit(map.getFwdSoftLimEnabled());
		canTalon.setForwardSoftLimit(map.getFwdSoftLimVal());
		canTalon.enableReverseSoftLimit(map.getRevSoftLimEnabled());
		canTalon.setReverseSoftLimit(map.getRevSoftLimVal());
		canTalon.enableBrakeMode(map.getBrakeMode());

		//High gear speed is the default
		maxSpeed = map.getMaxSpeedHg();

		//We have to do some weird things to convert out of the enum in the proto and into the CANTalon enum.
		feedbackDevice = CANTalon.FeedbackDevice.valueOf(map.getFeedbackDevice().getNumber());
		canTalon.setFeedbackDevice(feedbackDevice);

		//Configure the minimum output voltage. Should be symmetrical because it can go forwards and back.
		canTalon.configNominalOutputVoltage(+map.getNominalOutVoltage(), -map.getNominalOutVoltage());

		//Configure the maximum output voltage. Should be symmetrical because it can go forwards and back.
		canTalon.configPeakOutputVoltage(+map.getPeakOutVoltage(), -map.getPeakOutVoltage());

		//Initialize the PID constants in slot 0 to the high gear ones.
		setPIDF(map.getKPHg(), map.getKIHg(), map.getKDHg(), maxSpeed, 0, 0, 0);

		//If we have motion profile PID constants, put them in slot 1.
		//TODO stop hard-coding in which gear's max speed to use.
		if (map.hasKPMp()) {
			setPIDF(map.getKPMp(), map.getKIMp(), map.getKDMp(), map.getMaxSpeedLg(), 0, 0, 1);
		}

		//Choose which profile to use, regular driving or MP.
		canTalon.setProfile(map.getProfile());
	}

	/**
	 * Give a PercentVbus setpoint (set to PercentVbus mode and set)
	 *
	 * @param percentVbus percent of total voltage (between -1.0 and +1.0)
	 */
	public void setPercentVbus(double percentVbus) {
		//Warn the user if they're setting Vbus to a number that's outside the range of values.
		if (Math.abs(percentVbus) > 1.0) {
			System.out.println("WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVbus);
			percentVbus = Math.signum(percentVbus);
		}

		//Switch to voltage mode
		canTalon.changeControlMode(CANTalon.TalonControlMode.PercentVbus);

		//Set the setpoint to the input given.
		canTalon.set(percentVbus);
	}

	/**
	 * Set up all the PIDF constants, using a maxSpeed insead of an F value.
	 *
	 * @param p                 The proportional gain term of the loop
	 * @param i                 The integral gain term of the loop
	 * @param d                 The derivative gain term of the loop
	 * @param maxSpeed          The max speed of this motor, in RPS
	 * @param iZone             Integration zone -- prevents accumulation of integration error with large errors.
	 *                             Setting this to zero will ignore any izone stuff.
	 * @param closeLoopRampRate Closed loop ramp rate. Maximum change in voltage, in volts / sec.
	 * @param profile           The profile to use (must be 0 or 1).
	 */
	private void setPIDF(double p, double i, double d, double maxSpeed, int iZone, double closeLoopRampRate, int
			profile) {
		this.canTalon.setPID(p, i, d, 1023 / RPStoNative(maxSpeed), iZone, closeLoopRampRate, profile);
	}

	/**
	 * Switch to using the high gear PID constants.
	 */
	public void switchToHighGear() {
		//Switch max speed to high gear max speed
		maxSpeed = map.getMaxSpeedHg();
		//Set the slot 0 constants to the high gear ones.
		setPIDF(map.getKPHg(), map.getKIHg(), map.getKDHg(), maxSpeed, 0, 0, 0);
	}

	/**
	 * Switch to using the low gear PID constants.
	 */
	public void switchToLowGear() {
		//If there are low gear constants in the map
		if (map.hasKPLg()) {
			//Switch max speed to low gear max speed
			maxSpeed = map.getMaxSpeedLg();
			//Set the slot 0 constants to the low gear ones.
			setPIDF(map.getKPLg(), map.getKILg(), map.getKDLg(), maxSpeed, 0, 0, 0);
		} else {
			//Warn the user if they're trying to do this but don't have the low gear constants in the map.
			System.out.println("You're trying to switch your PIDF constants to low gear, but you don't have low gear " +
					"constants.");
		}
	}

	/**
	 * Get the max speed of the gear the talon is currently in.
	 * @return max speed, in RPS, as given in the map.
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Convert from RPS to the CANTalon native velocity units
	 *
	 * @param RPS The RPS velocity you want to convert
	 * @return That velocity in CANTalon native units
	 */
	public double RPStoNative(double RPS) {
		return (RPS / 10) * (encoderCPR * 4); //4 edges per count, and 10 100ms per second.
	}

	/**
	 * Convert from CANTalon native velocity units to rotations per second.
	 *
	 * @param nat A velocity in CANTalon native units
	 * @return That velocity in RPS
	 */
	public double nativeToRPS(double nat) {
		return (nat / (encoderCPR * 4)) * 10; //4 edges per count, and 10 100ms per second.
	}

	/**
	 * Get the velocity of the CANTalon in RPS
	 * <p>
	 * Note: This method is called getSpeed since the TalonControlMode enum is called speed. However, the output
	 * is signed and is actually a velocity.
	 *
	 * @return The CANTalon's velocity in RPS
	 */
	public double getSpeed() {
		//If we use a CTRE encoder, it returns in rotations per minute
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative || feedbackDevice == CANTalon
				.FeedbackDevice.CtreMagEncoder_Absolute) {
			return canTalon.getSpeed() / 60;
		}
		//Otherwise, convert from natives.
		return nativeToRPS(canTalon.getSpeed());
	}

	/**
	 * Give a velocity closed loop setpoint in RPS
	 * <p>
	 * Note: This method is called setSpeed since the TalonControlMode enum is called speed. However, the input
	 * argument is signed and is actually a velocity.
	 *
	 * @param velocitySp velocity setpoint in revolutions per second
	 */
	public void setSpeed(double velocitySp) {
		//Switch control mode to speed closed-loop
		canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		//If we use a CTRE encoder, it takes rotations per minute
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative || feedbackDevice == CANTalon
				.FeedbackDevice.CtreMagEncoder_Absolute) {
			canTalon.set(velocitySp * 60);
		} else {
			//Otherwise, convert to natives.
			canTalon.set(RPStoNative(velocitySp));
		}
	}

	/**
	 * Set which slot the Talon reads the PID gains from
	 *
	 * @param slot gains slot (0 or 1)
	 * @throws IllegalArgumentException if the specified slot isn't 0 or 1
	 */
	@Deprecated
	public void setPSlot(int slot) throws IllegalArgumentException {
		canTalon.setProfile(slot);
	}

	/**
	 * Get the current closed-loop velocity error in RPS. WARNING: will give garbage if not in velocity mode.
	 *
	 * @return The closed-loop error in RPS
	 */
	public double getError() {
		return nativeToRPS(canTalon.getError());
	}

	/**
	 * Get the high gear max speed. Sometimes useful for scaling joystick output.
	 *
	 * @return The high gear max speed in RPS
	 */
	public double getMaxSpeedHG() {
		return map.getMaxSpeedHg();
	}
}
