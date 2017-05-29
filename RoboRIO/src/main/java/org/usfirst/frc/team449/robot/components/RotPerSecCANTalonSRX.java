package org.usfirst.frc.team449.robot.components;

import com.ctre.CANTalon;
import maps.org.usfirst.frc.team449.robot.components.MotorMap;
import maps.org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRXMap;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Component wrapper on the CTRE {@link CANTalon}, with unit conversions to/from RPS built in. Every
 * non-unit-conversion in this class takes arguments in post-gearing RPS.
 */
public class RotPerSecCANTalonSRX extends Component {

	/**
	 * The CTRE CAN Talon SRX that this class is a wrapper on
	 */
	public CANTalon canTalon;

	/**
	 * The counts per rotation of the encoder being used.
	 */
	private int encoderCPR;

	/**
	 * The maximum speed of the motor, in RPS.
	 */
	private double maxSpeed;

	/**
	 * The type of encoder the talon uses.
	 */
	private CANTalon.FeedbackDevice feedbackDevice;

	/**
	 * The map used to construct this object.
	 */
	private RotPerSecCANTalonSRXMap.RotPerSecCANTalonSRX map;

	/**
	 * The coefficient the output changes by after being measured by the encoder, e.g. this would be 1/70 if
	 * there was a 70:1 gearing between the encoder and the final output.
	 */
	private double postEncoderGearing;

	/**
	 * The diameter of the wheel this is attached to. Only used for Motion Profile unit conversions.
	 * {@link Double} so it throws a nullPointer if you try to use it without a value in the map.
	 */
	private Double wheelDiameterInches;

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
		postEncoderGearing = map.getPostEncoderGearing();

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

		//Assume regular driving profile by default.
		canTalon.setProfile(0);

		//Configure optional parameters
		if (map.hasClosedLoopRampRate()) {
			canTalon.setCloseLoopRampRate(map.getClosedLoopRampRate());
		}

		if (map.hasWheelDiameterInches()) {
			wheelDiameterInches = map.getWheelDiameterInches();
		}

		//If we have motion profile PID constants, put them in slot 1.
		if (map.hasKPMp()) {
			//If we have a low gear and want to use it for MP, set the MP max speed to the low gear max.
			if (map.hasMaxSpeedLg() && map.getMpUseLowGear()) {
				setPIDF(map.getKPMp(), map.getKIMp(), map.getKDMp(), map.getMaxSpeedLg(), 0, 0, 1);
			} else {
				//Otherwise, use high gear.
				setPIDF(map.getKPMp(), map.getKIMp(), map.getKDMp(), map.getMaxSpeedHg(), 0, 0, 1);
			}
		}

		if (map.hasCurrentLimit()) {
			canTalon.setCurrentLimit(map.getCurrentLimit());
			canTalon.EnableCurrentLimit(true);
		} else {
			//If we don't have a current limit, disable current limiting.
			canTalon.EnableCurrentLimit(false);
		}

		//Set up slaves.
		for (MotorMap.Motor slave : map.getSlaveList()) {
			CANTalon tmp = new CANTalon(slave.getPort());
			tmp.changeControlMode(CANTalon.TalonControlMode.Follower);
			tmp.set(map.getPort());
			//To invert slaves, use reverseOutput. See section 9.1.4 of the TALON SRX Software Reference Manual.
			tmp.reverseOutput(slave.getInverted());
		}
	}

	/**
	 * Give a PercentVbus setpoint (set to PercentVbus mode and set)
	 *
	 * @param percentVbus percent of total voltage from [-1, 1]
	 */
	public void setPercentVbus(double percentVbus) {
		//Warn the user if they're setting Vbus to a number that's outside the range of values.
		if (Math.abs(percentVbus) > 1.0) {
			Logger.addEvent("WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVbus, this.getClass());
			percentVbus = Math.signum(percentVbus);
		}

		//Switch to voltage mode
		canTalon.changeControlMode(CANTalon.TalonControlMode.PercentVbus);

		//Set the setpoint to the input given.
		canTalon.set(percentVbus);
	}

	/**
	 * Set up all the PIDF constants, using a maxSpeed instead of an F value.
	 *
	 * @param p                 The proportional gain term of the loop
	 * @param i                 The integral gain term of the loop
	 * @param d                 The derivative gain term of the loop
	 * @param maxSpeed          The max speed of this motor, in RPS
	 * @param iZone             Integration zone -- prevents accumulation of integration error with large errors.
	 *                          Setting this to zero will ignore any izone stuff.
	 * @param closeLoopRampRate Closed loop ramp rate. Maximum change in voltage, in volts / sec.
	 * @param profile           The profile to use (must be 0 or 1).
	 */
	private void setPIDF(double p, double i, double d, double maxSpeed, int iZone, double closeLoopRampRate, int
			profile) {
		this.canTalon.setPID(p, i, d, 1023 / RPSToNative(maxSpeed), iZone, closeLoopRampRate, profile);
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
	 * Switch to using the low gear PID constants if we have them.
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
			Logger.addEvent("You're trying to switch your PIDF constants to low gear, but you don't have low gear " +
					"constants.", this.getClass());
		}
	}

	/**
	 * Get the max speed of the gear the talon is currently in.
	 *
	 * @return max speed, in RPS, as given in the map.
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Converts the velocity read by the talon's getSpeed() method to the RPS of the output shaft.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param encoderReading The velocity read from the encoder with no conversions.
	 * @return The velocity of the output shaft, in RPS, when the encoder has that reading.
	 */
	public double encoderToRPS(double encoderReading) {
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Absolute || feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative) {
			return RPMToRPS(encoderReading) * postEncoderGearing;
		} else {
			return nativeToRPS(encoderReading) * postEncoderGearing;
		}
	}

	/**
	 * Converts from the velocity of the output shaft to what the talon's getSpeed() method would read at that velocity.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param RPS The velocity of the output shaft, in RPS.
	 * @return What the raw encoder reading would be at that velocity.
	 */
	public double RPSToEncoder(double RPS) {
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Absolute || feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative) {
			return RPSToRPM(RPS) / postEncoderGearing;
		} else {
			return RPSToNative(RPS) / postEncoderGearing;
		}
	}

	/**
	 * Convert from output RPS to the CANTalon native velocity units. Note this DOES NOT account for post-encoder
	 * gearing.
	 *
	 * @param RPS The RPS velocity you want to convert.
	 * @return That velocity in CANTalon native units.
	 */
	private double RPSToNative(double RPS) {
		return (RPS / 10) * (encoderCPR * 4); //4 edges per count, and 10 100ms per second.
	}

	/**
	 * Convert from CANTalon native velocity units to output rotations per second. Note this DOES NOT account for
	 * post-encoder gearing.
	 *
	 * @param nat A velocity in CANTalon native units.
	 * @return That velocity in RPS.
	 */
	private double nativeToRPS(double nat) {
		return (nat / (encoderCPR * 4)) * 10; //4 edges per count, and 10 100ms per second.
	}

	/**
	 * Convert from RPM to RPS. Note this DOES NOT account for post-encoder gearing.
	 *
	 * @param rpm A velocity in RPM.
	 * @return That velocity in RPS.
	 */
	private double RPMToRPS(double rpm) {
		return rpm / 60.;
	}

	/**
	 * Convert from RPS to RPM. Note this DOES NOT account for post-encoder gearing.
	 *
	 * @param rps A velocity in RPS.
	 * @return That velocity in RPM.
	 */
	private double RPSToRPM(double rps) {
		return rps * 60.;
	}

	/**
	 * Get the velocity of the CANTalon in RPS
	 * <p>
	 * Note: This method is called getMode since the TalonControlMode enum is called speed. However, the output
	 * is signed and is actually a velocity.
	 *
	 * @return The CANTalon's velocity in RPS
	 */
	public double getSpeed() {
		return encoderToRPS(canTalon.getSpeed());
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
		canTalon.set(RPSToEncoder(velocitySp));
	}

	/**
	 * Get the current closed-loop velocity error in RPS. WARNING: will give garbage if not in velocity mode.
	 *
	 * @return The closed-loop error in RPS
	 */
	public double getError() {
		return encoderToRPS(canTalon.getError());
	}

	/**
	 * Get the current velocity setpoint of the Talon in RPS. WARNING: will give garbage if not in velocity mode.
	 *
	 * @return The closed-loop velocity setpoint in RPS.
	 */
	public double getSetpoint() {
		return encoderToRPS(canTalon.getSetpoint());
	}

	/**
	 * Get the high gear max speed. Sometimes useful for scaling joystick output.
	 *
	 * @return The high gear max speed in RPS.
	 */
	public double getMaxSpeedHG() {
		return map.getMaxSpeedHg();
	}

	/**
	 * Get the amount of power the Talon is currently drawing from the PDP.
	 *
	 * @return Power in watts.
	 */
	public double getPower() {
		return canTalon.getOutputVoltage() * canTalon.getOutputCurrent();
	}

	/**
	 * Convert from native units read by an encoder to feet moved.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param nativeUnits A distance native units as measured by the encoder.
	 * @return That distance in feet.
	 */
	public double nativeToFeet(double nativeUnits) {
		double rotations = nativeUnits / (encoderCPR * 4) * postEncoderGearing;
		return rotations * (wheelDiameterInches * Math.PI);
	}

	/**
	 * Convert a distance from feet to encoder reading in native units.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param feet A distance in feet.
	 * @return That distance in native units as measured by the encoder.
	 */
	public double feetToNative(double feet) {
		double rotations = feet / (wheelDiameterInches * Math.PI);
		return rotations * (encoderCPR * 4) / postEncoderGearing;
	}

	/**
	 * Convert a velocity from feet per second to encoder units.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param fps A velocity in feet per second
	 * @return That velocity in either native units or RPS, depending on the type of encoder.
	 */
	public double feetPerSecToNative(double fps) {
		return RPSToEncoder(fps / (wheelDiameterInches * Math.PI));
	}

	/**
	 * Convert a velocity from encoder units to feet per second.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param nativeUnits A velocity in either native units or RPS, depending on the type of encoder.
	 * @return That velocity in feet per second.
	 */
	public double nativeToFeetPerSec(double nativeUnits) {
		return encoderToRPS(nativeUnits) * (wheelDiameterInches * Math.PI);
	}
}
