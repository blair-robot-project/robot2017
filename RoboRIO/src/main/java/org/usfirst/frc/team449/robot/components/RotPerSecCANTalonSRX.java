package org.usfirst.frc.team449.robot.components;

import com.ctre.CANTalon;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Contract;
import org.usfirst.frc.team449.robot.util.Logger;

import java.util.List;

/**
 * Component wrapper on the CTRE {@link CANTalon}, with unit conversions to/from RPS built in. Every
 * non-unit-conversion in this class takes arguments in post-gearing RPS.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RotPerSecCANTalonSRX {

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
	 * The coefficient the output changes by after being measured by the encoder, e.g. this would be 1/70 if
	 * there was a 70:1 gearing between the encoder and the final output.
	 */
	private double postEncoderGearing;

	/**
	 * The number of inches travelled per rotation of the motor this is attached to. Only used for Motion Profile unit conversions.
	 * {@link Double} so it throws a nullPointer if you try to use it without a value in the map.
	 */
	private Double inchesPerRotation;

	/**
	 * The max speed of this motor, in RPS, when in high gear, or if the output motor doesn't have gears,
	 * just the max speed. May be null.
	 */
	private Double maxSpeedHigh;

	/**
	 * If this motor has a low gear, this is the max speed of this motor when in that gear. Otherwise, null.
	 */
	private Double maxSpeedLow;

	/**
	 * The PID constants for high gear or, if this motor does not have gears, just the PID constants.
	 */
	private int highGearP, highGearI, highGearD;

	/**
	 * The PID constants for low gear if this motor has a low gear.
	 */
	private int lowGearP, lowGearI, lowGearD;

	/**
	 * Default constructor.
	 *
	 * @param port                       CAN port of this Talon.
	 * @param inverted                   Whether this Talon is inverted.
	 * @param enableBrakeMode            Whether to brake or coast when stopped.
	 * @param fwdPeakOutputVoltage       The peak voltage in the forward direction, in volts. If revPeakOutputVoltage is
	 *                                   null,
	 *                                   this is used for peak voltage in both directions. Should be a positive or
	 *                                   zero.
	 * @param revPeakOutputVoltage       The peak voltage in the reverse direction. Can be null, and if it is,
	 *                                   fwdPeakOutputVoltage is used as the peak voltage in both directions.
	 *                                   Should be positive or zero.
	 * @param fwdNominalOutputVoltage    The minimum non-zero voltage in the forward direction, in volts.
	 *                                   If revNominalOutputVoltage is null, this is used for nominal voltage in both
	 *                                   directions. Should be a positive or zero.
	 * @param revNominalOutputVoltage    The minimum non-zero voltage in the reverse direction. Can be null, and if it
	 *                                   is,
	 *                                   fwdNominalOutputVoltage is used as the nominal voltage in both directions.
	 *                                   Should be positive or zero.
	 * @param fwdLimitSwitchNormallyOpen Whether the forward limit switch is normally open or closed. If this is null,
	 *                                   the forward limit switch is disabled.
	 * @param revLimitSwitchNormallyOpen Whether the reverse limit switch is normally open or closed. If this is null,
	 *                                   the reverse limit switch is disabled.
	 * @param fwdSoftLimit               The forward software limit. If this is null, the forward software limit is
	 *                                   disabled.
	 *                                   TODO figure out units
	 * @param revSoftLimit               The reverse software limit. If this is null, the reverse software limit is
	 *                                   disabled.
	 *                                   TODO figure out units
	 * @param postEncoderGearing         The coefficient the output changes by after being measured by the encoder, e.g.
	 *                                   this
	 *                                   would be 1/70 if there was a 70:1 gearing between the encoder and the final
	 *                                   output.
	 *                                   Defaults to 1.
	 * @param closedLoopRampRate         The voltage ramp rate for closed-loop velocity control. Can be null, and if it
	 *                                   is, no
	 *                                   ramp rate is used.
	 * @param inchesPerRotation          The number of inches travelled per rotation of the motor this is attached to.
	 * @param currentLimit               The max amps this device can draw. If this is null, no current limit is used.
	 * @param feedbackDevice             The type of encoder used to measure the output velocity of this motor. Can be
	 *                                   null if there
	 *                                   is no encoder attached to this Talon.
	 * @param encoderCPR                 The counts per rotation of the encoder on this Talon. Can be null if
	 *                                   feedbackDevice is, but
	 *                                   otherwise must have a value.
	 * @param reverseSensor              Whether or not to reverse the reading from the encoder on this Talon. Can be
	 *                                   null if
	 *                                   feedbackDevice is, but otherwise must have a value.
	 * @param maxSpeedHigh               The high gear max speed, in RPS. If this motor doesn't have gears, then this is
	 *                                   just the max speed. Used to calculate velocity PIDF feed-forward. Can be null,
	 *                                   and if it is, it's assumed that this motor won't use velocity closed-loop
	 *                                   control.
	 * @param highGearP The proportional gain for high gear. Defaults to 0.
	 * @param highGearI The integral gain for high gear. Defaults to 0.
	 * @param highGearD The derivative gain for high gear. Defaults to 0.
	 * @param maxSpeedLow                The low gear max speed in RPS. Used to calculate velocity PIDF feed-forward.
	 *                                   Can be null, and
	 *                                   if it is, it's assumed that either this motor doesn't have a low gear or the
	 *                                   low gear won't
	 *                                   use velocity closed-loop control.
	 * @param lowGearP The proportional gain for low gear. Defaults to 0.
	 * @param lowGearI The integral gain for low gear. Defaults to 0.
	 * @param lowGearD The derivative gain for low gear. Defaults to 0.
	 * @param motionProfileP The proportional gain for motion profiles. Defaults to 0.
	 * @param motionProfileI The integral gain for high motion profiles. Defaults to 0.
	 * @param motionProfileD The derivative gain for high motion profiles. Defaults to 0.
	 * @param MPUseLowGear               Whether this motor uses high or low gear for running motion profiles. Defaults
	 *                                   to false.
	 * @param slaves                     The other {@link CANTalon}s that are slaved to this one.
	 */
	@JsonCreator
	public RotPerSecCANTalonSRX(@JsonProperty(required = true) int port,
	                            @JsonProperty(required = true) boolean inverted,
	                            @JsonProperty(required = true) boolean enableBrakeMode,
	                            @JsonProperty(required = true) double fwdPeakOutputVoltage,
	                            Double revPeakOutputVoltage,
	                            @JsonProperty(required = true) double fwdNominalOutputVoltage,
	                            Double revNominalOutputVoltage,
	                            Boolean fwdLimitSwitchNormallyOpen,
	                            Boolean revLimitSwitchNormallyOpen,
	                            Double fwdSoftLimit,
	                            Double revSoftLimit,
	                            Double postEncoderGearing,
	                            Double closedLoopRampRate,
	                            Double inchesPerRotation,
	                            Integer currentLimit,
	                            CANTalon.FeedbackDevice feedbackDevice,
	                            Integer encoderCPR,
	                            Boolean reverseSensor,
	                            Double maxSpeedHigh,
	                            int highGearP,
	                            int highGearI,
	                            int highGearD,
	                            Double maxSpeedLow,
	                            int lowGearP,
	                            int lowGearI,
	                            int lowGearD,
	                            int motionProfileP,
	                            int motionProfileI,
	                            int motionProfileD,
	                            boolean MPUseLowGear,
	                            List<SlaveTalon> slaves) {
		//Instantiate the base CANTalon this is a wrapper on.
		canTalon = new CANTalon(port);
		//Set this to false because we only use reverseOutput for slaves.
		canTalon.reverseOutput(false);
		//Set inversion
		canTalon.setInverted(inverted);
		//Set brake mode
		canTalon.enableBrakeMode(enableBrakeMode);

		//Only enable the limit switches if it was specified if they're normally open or closed.
		boolean fwdSwitchEnable = false, revSwitchEnable = false;
		if (fwdLimitSwitchNormallyOpen != null) {
			canTalon.ConfigFwdLimitSwitchNormallyOpen(fwdLimitSwitchNormallyOpen);
			fwdSwitchEnable = true;
		}
		if (revLimitSwitchNormallyOpen != null) {
			canTalon.ConfigRevLimitSwitchNormallyOpen(revLimitSwitchNormallyOpen);
			revSwitchEnable = true;
		}
		canTalon.enableLimitSwitch(fwdSwitchEnable, revSwitchEnable);

		//Only enable the software limits if they were given a value.
		if (fwdSoftLimit != null) {
			canTalon.enableForwardSoftLimit(true);
			canTalon.setForwardSoftLimit(fwdSoftLimit);
		} else {
			canTalon.enableForwardSoftLimit(false);
		}
		if (revSoftLimit != null) {
			canTalon.enableReverseSoftLimit(true);
			canTalon.setReverseSoftLimit(revSoftLimit);
		} else {
			canTalon.enableReverseSoftLimit(false);
		}

		//Set up the feedback device if it exists.
		if (feedbackDevice != null) {
			this.feedbackDevice = feedbackDevice;
			canTalon.setFeedbackDevice(feedbackDevice);
			this.encoderCPR = encoderCPR;
			canTalon.reverseSensor(reverseSensor);
		}

		//postEncoderGearing defaults to 1
		if (postEncoderGearing == null) {
			postEncoderGearing = 1.;
		}
		this.postEncoderGearing = postEncoderGearing;

		//Configure the nominal output voltage. If only forward voltage was given, use it for both forward and reverse.
		if (revNominalOutputVoltage == null) {
			revNominalOutputVoltage = fwdNominalOutputVoltage;
		}
		canTalon.configNominalOutputVoltage(fwdNominalOutputVoltage, -revNominalOutputVoltage);

		//Configure the maximum output voltage. If only forward voltage was given, use it for both forward and reverse.
		if (revPeakOutputVoltage == null) {
			revPeakOutputVoltage = fwdPeakOutputVoltage;
		}
		canTalon.configPeakOutputVoltage(fwdPeakOutputVoltage, -revPeakOutputVoltage);

		//Set the current limit if it was given
		if (currentLimit != null) {
			canTalon.setCurrentLimit(currentLimit);
			canTalon.EnableCurrentLimit(true);
		} else {
			//If we don't have a current limit, disable current limiting.
			canTalon.EnableCurrentLimit(false);
		}

		//Configure ramp rate
		if (closedLoopRampRate != null) {
			canTalon.setCloseLoopRampRate(closedLoopRampRate);
		}

		//We can set this directly because the field is also a Double and can be null.
		this.inchesPerRotation = inchesPerRotation;

		//Set fields
		this.maxSpeedHigh = maxSpeedHigh;
		this.highGearP = highGearP;
		this.highGearI = highGearI;
		this.highGearD = highGearD;
		this.maxSpeedLow = maxSpeedLow;
		this.lowGearP = lowGearP;
		this.lowGearI = lowGearI;
		this.lowGearD = lowGearD;

		//Set up PID constants.
		if (maxSpeedHigh != null) {
			//High gear speed is the default
			maxSpeed = maxSpeedHigh;

			//Initialize the PID constants in slot 0 to the high gear ones.
			setPIDF(highGearP, highGearI, highGearD, maxSpeed, 0, 0, 0);

			//Assume regular driving profile by default.
			canTalon.setProfile(0);

			//Add Motion Profile PID constants if we have them.
			if (motionProfileP != 0) {
				//If we have a low gear and want to use it for MP, set the MP max speed to the low gear max.
				if (maxSpeedLow != null && MPUseLowGear) {
					setPIDF(motionProfileP, motionProfileI, motionProfileD, maxSpeedLow, 0, 0, 1);
				} else {
					//Otherwise, use high gear.
					setPIDF(motionProfileP, motionProfileI, motionProfileD, maxSpeed, 0, 0, 1);
				}
			}
		}

		if (slaves != null) {
			//Set up slaves.
			for (SlaveTalon slave : slaves) {
				CANTalon tmp = new CANTalon(slave.getPort());
				//To invert slaves, use reverseOutput. See section 9.1.4 of the TALON SRX Software Reference Manual.
				tmp.reverseOutput(slave.isInverted());
				//Don't use the other inversion options
				tmp.reverseSensor(false);
				tmp.setInverted(false);

				//Brake mode and current limiting don't automatically follow master, so we set them up for each slave.
				tmp.enableBrakeMode(enableBrakeMode);
				if (currentLimit != null) {
					canTalon.setCurrentLimit(currentLimit);
					canTalon.EnableCurrentLimit(true);
				} else {
					//If we don't have a current limit, disable current limiting.
					canTalon.EnableCurrentLimit(false);
				}

				//Don't forget to enable!
				tmp.enable();
				//Set the slave up to follow this talon.
				tmp.changeControlMode(CANTalon.TalonControlMode.Follower);
				tmp.set(port);
			}
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
		if (maxSpeedHigh != null) {
			//Switch max speed to high gear max speed
			maxSpeed = maxSpeedHigh;
			//Set the slot 0 constants to the high gear ones.
			setPIDF(highGearP, highGearI, highGearD, maxSpeed, 0, 0, 0);
		} else {
			//Warn the user if they're trying to do this but don't have the PID constants in the map.
			Logger.addEvent("You're trying to switch your PIDF constants to high gear, but you don't have high gear " +
					"constants.", this.getClass());
		}
	}

	/**
	 * Switch to using the low gear PID constants if we have them.
	 */
	public void switchToLowGear() {
		//If there are low gear constants in the map
		if (maxSpeedLow != null) {
			//Switch max speed to low gear max speed
			maxSpeed = maxSpeedLow;
			//Set the slot 0 constants to the low gear ones.
			setPIDF(lowGearP, lowGearI, lowGearD, maxSpeed, 0, 0, 0);
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
	@Contract(pure = true)
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
	@Contract(pure = true)
	private double nativeToRPS(double nat) {
		return (nat / (encoderCPR * 4)) * 10; //4 edges per count, and 10 100ms per second.
	}

	/**
	 * Convert from RPM to RPS. Note this DOES NOT account for post-encoder gearing.
	 *
	 * @param rpm A velocity in RPM.
	 * @return That velocity in RPS.
	 */
	@Contract(pure = true)
	private double RPMToRPS(double rpm) {
		return rpm / 60.;
	}

	/**
	 * Convert from RPS to RPM. Note this DOES NOT account for post-encoder gearing.
	 *
	 * @param rps A velocity in RPS.
	 * @return That velocity in RPM.
	 */
	@Contract(pure = true)
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
		return maxSpeedHigh;
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
		return rotations * (inchesPerRotation /12.);
	}

	/**
	 * Convert a distance from feet to encoder reading in native units.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param feet A distance in feet.
	 * @return That distance in native units as measured by the encoder.
	 */
	public double feetToNative(double feet) {
		double rotations = feet / (inchesPerRotation / 12.);
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
		return RPSToEncoder(fps / (inchesPerRotation/12.));
	}

	/**
	 * Convert a velocity from encoder units to feet per second.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param nativeUnits A velocity in either native units or RPS, depending on the type of encoder.
	 * @return That velocity in feet per second.
	 */
	public double nativeToFeetPerSec(double nativeUnits) {
		return encoderToRPS(nativeUnits) * inchesPerRotation / 12.;
	}

	/**
	 * An object representing a slave {@link CANTalon} for use in the map.
	 */
	@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
	private static class SlaveTalon {

		/**
		 * The port number of this Talon.
		 */
		private int port;

		/**
		 * Whether this Talon is inverted compared to its master.
		 */
		private boolean inverted;

		/**
		 * Default constructor.
		 *
		 * @param port     The port number of this Talon.
		 * @param inverted Whether this Talon is inverted compared to its master.
		 */
		@JsonCreator
		public SlaveTalon(@JsonProperty(required = true) int port,
		                  @JsonProperty(required = true) boolean inverted) {
			this.port = port;
			this.inverted = inverted;
		}

		/**
		 * Getter for port number.
		 *
		 * @return The port number of this Talon.
		 */
		public int getPort() {
			return port;
		}

		/**
		 * Getter for inversion.
		 *
		 * @return true if this Talon is inverted compared to its master, false otherwise.
		 */
		public boolean isInverted() {
			return inverted;
		}
	}
}
