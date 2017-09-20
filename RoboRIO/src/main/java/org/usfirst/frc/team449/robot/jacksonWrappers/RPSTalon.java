package org.usfirst.frc.team449.robot.jacksonWrappers;

import com.ctre.CANTalon;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.generalInterfaces.shiftable.Shiftable;
import org.usfirst.frc.team449.robot.generalInterfaces.simpleMotor.SimpleMotor;
import org.usfirst.frc.team449.robot.logger.Logger;

import java.util.List;

/**
 * Component wrapper on the CTRE {@link CANTalon}, with unit conversions to/from RPS built in. Every non-unit-conversion
 * in this class takes arguments in post-gearing RPS.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RPSTalon implements SimpleMotor, Shiftable {

	/**
	 * The CTRE CAN Talon SRX that this class is a wrapper on
	 */
	@NotNull
	private final CANTalon canTalon;

	/**
	 * The counts per rotation of the encoder being used, or null if there is no encoder.
	 */
	@Nullable
	private final Integer encoderCPR;

	/**
	 * The type of encoder the talon uses, or null if there is no encoder.
	 */
	@Nullable
	private final CANTalon.FeedbackDevice feedbackDevice;

	/**
	 * The coefficient the output changes by after being measured by the encoder, e.g. this would be 1/70 if there was a
	 * 70:1 gearing between the encoder and the final output.
	 */
	private final double postEncoderGearing;

	/**
	 * The number of inches travelled per rotation of the motor this is attached to, or null if there is no encoder.
	 * Only used for Motion Profile unit conversions. {@link Double} so it throws a nullPointer if you try to use it
	 * without a value in the map.
	 */
	@Nullable
	private final Double inchesPerRotation;

	/**
	 * The max speed of this motor, in RPS, when in high gear, or if the output motor doesn't have gears, just the max
	 * speed. May be null.
	 */
	@Nullable
	private final Double maxSpeedHigh;

	/**
	 * If this motor has a low gear, this is the max speed of this motor when in that gear. Otherwise, null.
	 */
	@Nullable
	private final Double maxSpeedLow;

	/**
	 * The PID constants for high gear or, if this motor does not have gears, just the PID constants.
	 */
	private final double highGearP, highGearI, highGearD;

	/**
	 * The PID constants for low gear if this motor has a low gear.
	 */
	private final double lowGearP, lowGearI, lowGearD;

	/**
	 * The forward and reverse nominal voltages for high gear, or if this motor has no gears, just the nominal
	 * voltages.
	 */
	private final double highGearFwdNominalOutputVoltage, highGearRevNominalOutputVoltage;

	/**
	 * The forward and reverse nominal voltages for low gear, or null if this motor has no gears.
	 */
	@Nullable
	private final Double lowGearFwdNominalOutputVoltage, lowGearRevNominalOutputVoltage;

	/**
	 * The maximum speed of the motor, in RPS, or null if not using PID.
	 */
	@Nullable
	private Double maxSpeed;

	/**
	 * The current gear this Talon is in
	 */
	private Shiftable.gear currentGear;

	/**
	 * Default constructor.
	 *
	 * @param port                            CAN port of this Talon.
	 * @param inverted                        Whether this Talon is inverted.
	 * @param reverseOutput                   Whether to reverse the output (identical effect to inverting outside of
	 *                                        position PID)
	 * @param enableBrakeMode                 Whether to brake or coast when stopped.
	 * @param fwdPeakOutputVoltage            The peak voltage in the forward direction, in volts. If
	 *                                        revPeakOutputVoltage is null, this is used for peak voltage in both
	 *                                        directions. Should be a positive or zero.
	 * @param revPeakOutputVoltage            The peak voltage in the reverse direction. Can be null, and if it is,
	 *                                        fwdPeakOutputVoltage is used as the peak voltage in both directions.
	 *                                        Should be positive or zero.
	 * @param highGearFwdNominalOutputVoltage The minimum non-zero voltage in the forward direction in the high/only
	 *                                        gear, in volts. If highGearRevNominalOutputVoltage is null, this is used
	 *                                        for nominal voltage in both directions. Should be a positive or zero.
	 * @param highGearRevNominalOutputVoltage The minimum non-zero voltage in the reverse direction in the high/only
	 *                                        gear. Can be null, and if it is, highGearFwdNominalOutputVoltage is used
	 *                                        as the nominal voltage in both directions. Should be positive or zero.
	 * @param lowGearFwdNominalOutputVoltage  The minimum non-zero voltage in the forward direction in the high/only
	 *                                        gear, in volts. If highGearRevNominalOutputVoltage is null, this is used
	 *                                        for nominal voltage in both directions. Should be a positive or zero. Can
	 *                                        be null if this Talon doesn't have a low gear.
	 * @param lowGearRevNominalOutputVoltage  The minimum non-zero voltage in the reverse direction in the high/only
	 *                                        gear. Can be null, and if it is, highGearFwdNominalOutputVoltage is used
	 *                                        as the nominal voltage in both directions. Should be positive or zero.
	 * @param fwdLimitSwitchNormallyOpen      Whether the forward limit switch is normally open or closed. If this is
	 *                                        null, the forward limit switch is disabled.
	 * @param revLimitSwitchNormallyOpen      Whether the reverse limit switch is normally open or closed. If this is
	 *                                        null, the reverse limit switch is disabled.
	 * @param fwdSoftLimit                    The forward software limit. If this is null, the forward software limit is
	 *                                        disabled. TODO figure out units
	 * @param revSoftLimit                    The reverse software limit. If this is null, the reverse software limit is
	 *                                        disabled. TODO figure out units
	 * @param postEncoderGearing              The coefficient the output changes by after being measured by the encoder,
	 *                                        e.g. this would be 1/70 if there was a 70:1 gearing between the encoder
	 *                                        and the final output. Defaults to 1.
	 * @param closedLoopRampRate              The voltage ramp rate for closed-loop velocity control. Can be null, and
	 *                                        if it is, no ramp rate is used.
	 * @param inchesPerRotation               The number of inches travelled per rotation of the motor this is attached
	 *                                        to.
	 * @param currentLimit                    The max amps this device can draw. If this is null, no current limit is
	 *                                        used.
	 * @param maxClosedLoopVoltage            The voltage to scale closed-loop output based on, e.g. closed-loop output
	 *                                        of 1 will produce this voltage, output of 0.5 will produce half, etc. This
	 *                                        feature compensates for low battery voltage.
	 * @param feedbackDevice                  The type of encoder used to measure the output velocity of this motor. Can
	 *                                        be null if there is no encoder attached to this Talon.
	 * @param encoderCPR                      The counts per rotation of the encoder on this Talon. Can be null if
	 *                                        feedbackDevice is, but otherwise must have a value.
	 * @param reverseSensor                   Whether or not to reverse the reading from the encoder on this Talon. Can
	 *                                        be null if feedbackDevice is, but otherwise must have a value.
	 * @param maxSpeedHigh                    The high gear max speed, in RPS. If this motor doesn't have gears, then
	 *                                        this is just the max speed. Used to calculate velocity PIDF feed-forward.
	 *                                        Can be null, and if it is, it's assumed that this motor won't use velocity
	 *                                        closed-loop control.
	 * @param highGearP                       The proportional gain for high gear. Defaults to 0.
	 * @param highGearI                       The integral gain for high gear. Defaults to 0.
	 * @param highGearD                       The derivative gain for high gear. Defaults to 0.
	 * @param maxSpeedLow                     The low gear max speed in RPS. Used to calculate velocity PIDF
	 *                                        feed-forward. Can be null, and if it is, it's assumed that either this
	 *                                        motor doesn't have a low gear or the low gear won't use velocity
	 *                                        closed-loop control.
	 * @param lowGearP                        The proportional gain for low gear. Defaults to 0.
	 * @param lowGearI                        The integral gain for low gear. Defaults to 0.
	 * @param lowGearD                        The derivative gain for low gear. Defaults to 0.
	 * @param motionProfileP                  The proportional gain for motion profiles. Defaults to 0.
	 * @param motionProfileI                  The integral gain for high motion profiles. Defaults to 0.
	 * @param motionProfileD                  The derivative gain for high motion profiles. Defaults to 0.
	 * @param MPUseLowGear                    Whether this motor uses high or low gear for running motion profiles.
	 *                                        Defaults to false.
	 * @param slaves                          The other {@link CANTalon}s that are slaved to this one.
	 */
	@JsonCreator
	public RPSTalon(@JsonProperty(required = true) int port,
	                @JsonProperty(required = true) boolean inverted,
	                boolean reverseOutput,
	                @JsonProperty(required = true) boolean enableBrakeMode,
	                @JsonProperty(required = true) double fwdPeakOutputVoltage,
	                @Nullable Double revPeakOutputVoltage,
	                @JsonProperty(required = true) double highGearFwdNominalOutputVoltage,
	                @Nullable Double highGearRevNominalOutputVoltage,
	                @Nullable Double lowGearFwdNominalOutputVoltage,
	                @Nullable Double lowGearRevNominalOutputVoltage,
	                @Nullable Boolean fwdLimitSwitchNormallyOpen,
	                @Nullable Boolean revLimitSwitchNormallyOpen,
	                @Nullable Double fwdSoftLimit,
	                @Nullable Double revSoftLimit,
	                @Nullable Double postEncoderGearing,
	                @Nullable Double closedLoopRampRate,
	                @Nullable Double inchesPerRotation,
	                @Nullable Integer currentLimit,
	                double maxClosedLoopVoltage,
	                @Nullable CANTalon.FeedbackDevice feedbackDevice,
	                @Nullable Integer encoderCPR,
	                @Nullable Boolean reverseSensor,
	                @Nullable Double maxSpeedHigh,
	                double highGearP,
	                double highGearI,
	                double highGearD,
	                @Nullable Double maxSpeedLow,
	                double lowGearP,
	                double lowGearI,
	                double lowGearD,
	                double motionProfileP,
	                double motionProfileI,
	                double motionProfileD,
	                boolean MPUseLowGear,
	                @Nullable List<SlaveTalon> slaves) {
		//Instantiate the base CANTalon this is a wrapper on.
		canTalon = new CANTalon(port);
		//Set this to false because we only use reverseOutput for slaves.
		canTalon.reverseOutput(reverseOutput);
		//Set inversion
		canTalon.setInverted(inverted);
		//Set brake mode
		canTalon.enableBrakeMode(enableBrakeMode);

		//Set high/only gear nominal voltages
		this.highGearFwdNominalOutputVoltage = highGearFwdNominalOutputVoltage;
		//If no reverse nominal voltage is given, assume symmetry.
		this.highGearRevNominalOutputVoltage = highGearRevNominalOutputVoltage != null ? highGearRevNominalOutputVoltage : highGearFwdNominalOutputVoltage;

		//Set low gear nominal voltages
		this.lowGearFwdNominalOutputVoltage = lowGearFwdNominalOutputVoltage;
		//If no reverse nominal voltage is given, assume symmetry.
		this.lowGearRevNominalOutputVoltage = lowGearRevNominalOutputVoltage != null ? lowGearRevNominalOutputVoltage : lowGearFwdNominalOutputVoltage;

		//Set to high gear by default.
		canTalon.configNominalOutputVoltage(this.highGearFwdNominalOutputVoltage, -this.highGearRevNominalOutputVoltage);

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
		} else {
			this.feedbackDevice = null;
			this.encoderCPR = null;
		}

		//postEncoderGearing defaults to 1
		this.postEncoderGearing = postEncoderGearing != null ? postEncoderGearing : 1.;

		//Configure the maximum output voltage. If only forward voltage was given, use it for both forward and reverse.
		canTalon.configPeakOutputVoltage(fwdPeakOutputVoltage, revPeakOutputVoltage != null ? -revPeakOutputVoltage : -fwdPeakOutputVoltage);

		//Set the current limit if it was given
		if (currentLimit != null) {
			canTalon.setCurrentLimit(currentLimit);
			canTalon.EnableCurrentLimit(true);
		} else {
			//If we don't have a current limit, disable current limiting.
			canTalon.EnableCurrentLimit(false);
		}

		//Set the nominal closed loop battery voltage. Different thing from NominalOutputVoltage.
		canTalon.setNominalClosedLoopVoltage(maxClosedLoopVoltage);

		//Configure ramp rate
		if (closedLoopRampRate != null) {
			canTalon.setCloseLoopRampRate(closedLoopRampRate);
		}

		//Set fields
		this.maxSpeedHigh = maxSpeedHigh;
		this.highGearP = highGearP;
		this.highGearI = highGearI;
		this.highGearD = highGearD;
		this.maxSpeedLow = maxSpeedLow;
		this.lowGearP = lowGearP;
		this.lowGearI = lowGearI;
		this.lowGearD = lowGearD;
		this.inchesPerRotation = inchesPerRotation;

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

				tmp.enableLimitSwitch(false, false);
				tmp.enableForwardSoftLimit(false);
				tmp.enableReverseSoftLimit(false);
				tmp.configNominalOutputVoltage(0, 0);
				tmp.configPeakOutputVoltage(12, -12);
				tmp.configMaxOutputVoltage(12);

				//Brake mode and current limiting don't automatically follow master, so we set them up for each slave.
				tmp.enableBrakeMode(enableBrakeMode);
				if (currentLimit != null) {
					tmp.setCurrentLimit(currentLimit);
					tmp.EnableCurrentLimit(true);
				} else {
					//If we don't have a current limit, disable current limiting.
					tmp.EnableCurrentLimit(false);
				}

				//Set the slave up to follow this talon.
				tmp.changeControlMode(CANTalon.TalonControlMode.Follower);
				tmp.set(port);
				tmp.enable();
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
		try {
			this.canTalon.setPID(p, i, d, 1023 / RPSToNative(maxSpeed), iZone, closeLoopRampRate, profile);
		} catch (NullPointerException e) {
			System.out.println("Tried to set F value, but no encoder CPR given!");
			e.printStackTrace();
		}

	}

	/**
	 * @return The gear this subsystem is currently in.
	 */
	@NotNull
	@Override
	public gear getGear() {
		return currentGear;
	}

	/**
	 * Shift to a specific gear.
	 *
	 * @param gear Which gear to shift to.
	 */
	@Override
	public void setGear(@NotNull gear gear) {
		currentGear = gear;
		if (gear.equals(Shiftable.gear.HIGH)){
			canTalon.configNominalOutputVoltage(highGearFwdNominalOutputVoltage, -highGearRevNominalOutputVoltage);
			if (maxSpeedHigh != null) {
				//Switch max speed to high gear max speed
				maxSpeed = maxSpeedHigh;
				//Set the slot 0 constants to the high gear ones.
				setPIDF(highGearP, highGearI, highGearD, maxSpeed, 0, 0, 0);
			}
		} else {
			if (lowGearFwdNominalOutputVoltage != null) {
				canTalon.configNominalOutputVoltage(lowGearFwdNominalOutputVoltage, -lowGearRevNominalOutputVoltage);
			}
			//If there are low gear constants in the map
			if (maxSpeedLow != null) {
				//Switch max speed to low gear max speed
				maxSpeed = maxSpeedLow;
				//Set the slot 0 constants to the low gear ones.
				setPIDF(lowGearP, lowGearI, lowGearD, maxSpeed, 0, 0, 0);
			}
		}
	}

	/**
	 * @return the max speed of the gear the talon is currently in, in RPS, as given in the map, or null if no value
	 * given.
	 */
	@Nullable
	public Double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * Converts the velocity read by the talon's getSpeed() method to the RPS of the output shaft. Note this DOES
	 * account for post-encoder gearing.
	 *
	 * @param encoderReading The velocity read from the encoder with no conversions.
	 * @return The velocity of the output shaft, in RPS, when the encoder has that reading, or null if no encoder CPR
	 * was given.
	 */
	@Nullable
	public Double encoderToRPS(double encoderReading) {
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Absolute || feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative) {
			//CTRE encoders use RPM
			return RPMToRPS(encoderReading) * postEncoderGearing;
		} else {
			//All other feedback devices use native units.
			Double RPS = nativeToRPS(encoderReading);
			if (RPS == null) {
				return null;
			}
			return RPS * postEncoderGearing;
		}
	}

	/**
	 * Converts from the velocity of the output shaft to what the talon's getSpeed() method would read at that velocity.
	 * Note this DOES account for post-encoder gearing.
	 *
	 * @param RPS The velocity of the output shaft, in RPS.
	 * @return What the raw encoder reading would be at that velocity, or null if no encoder CPR was given.
	 */
	@Nullable
	public Double RPSToEncoder(double RPS) {
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Absolute || feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative) {
			//CTRE encoders use RPM
			return RPSToRPM(RPS) / postEncoderGearing;
		} else {
			//All other feedback devices use native units.
			Double encoderReading = RPSToNative(RPS);
			if (encoderReading == null) {
				return null;
			}
			return encoderReading / postEncoderGearing;
		}
	}

	/**
	 * Convert from output RPS to the CANTalon native velocity units. Note this DOES NOT account for post-encoder
	 * gearing.
	 *
	 * @param RPS The RPS velocity you want to convert.
	 * @return That velocity in CANTalon native units, or null if no encoder CPR was given.
	 */
	@Contract(pure = true)
	@Nullable
	private Double RPSToNative(double RPS) {
		if (encoderCPR == null) {
			return null;
		}
		return (RPS / 10) * (encoderCPR * 4); //4 edges per count, and 10 100ms per second.
	}

	/**
	 * Convert from CANTalon native velocity units to output rotations per second. Note this DOES NOT account for
	 * post-encoder gearing.
	 *
	 * @param nat A velocity in CANTalon native units.
	 * @return That velocity in RPS, or null if no encoder CPR was given.
	 */
	@Contract(pure = true)
	@Nullable
	private Double nativeToRPS(double nat) {
		if (encoderCPR == null) {
			return null;
		}
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
	 * Note: This method is called getSpeed since the {@link CANTalon} method is called getSpeed. However, the output is
	 * signed and is actually a velocity.
	 *
	 * @return The CANTalon's velocity in RPS, or null if no encoder CPR was given.
	 */
	@Nullable
	public Double getSpeed() {
		return encoderToRPS(canTalon.getSpeed());
	}

	/**
	 * Give a velocity closed loop setpoint in RPS
	 * <p>
	 * Note: This method is called setSpeed since the {@link CANTalon} method is called getSpeed. However, the input
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
	 * @return The closed-loop error in RPS, or null if no encoder CPR was given.
	 */
	@Nullable
	public Double getError() {
		return encoderToRPS(canTalon.getError());
	}

	/**
	 * Get the current velocity setpoint of the Talon in RPS. WARNING: will give garbage if not in velocity mode.
	 *
	 * @return The closed-loop velocity setpoint in RPS, or null if no encoder CPR was given.
	 */
	@Nullable
	public Double getSetpoint() {
		return encoderToRPS(canTalon.getSetpoint());
	}

	/**
	 * @return The high gear max speed in RPS, or null if none was given.
	 */
	@Nullable
	public Double getMaxSpeedHG() {
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
	 * Convert from native units read by an encoder to feet moved. Note this DOES account for post-encoder gearing.
	 *
	 * @param nativeUnits A distance native units as measured by the encoder.
	 * @return That distance in feet, or null if no encoder CPR or inches per rotation was given.
	 */
	@Nullable
	public Double nativeToFeet(double nativeUnits) {
		if (encoderCPR == null || inchesPerRotation == null) {
			return null;
		}
		double rotations = nativeUnits / (encoderCPR * 4) * postEncoderGearing;
		return rotations * (inchesPerRotation / 12.);
	}

	/**
	 * Convert a distance from feet to encoder reading in native units. Note this DOES account for post-encoder
	 * gearing.
	 *
	 * @param feet A distance in feet.
	 * @return That distance in native units as measured by the encoder, or null if no encoder CPR or inches per
	 * rotation was given.
	 */
	@Nullable
	public Double feetToNative(double feet) {
		if (encoderCPR == null || inchesPerRotation == null) {
			return null;
		}
		double rotations = feet / (inchesPerRotation / 12.);
		return rotations * (encoderCPR * 4) / postEncoderGearing;
	}

	/**
	 * Convert a velocity from feet per second to encoder units. Note this DOES account for post-encoder gearing.
	 *
	 * @param fps A velocity in feet per second
	 * @return That velocity in either native units or RPS, depending on the type of encoder, or null if no encoder CPR
	 * or inches per rotation was given.
	 */
	@Nullable
	public Double feetPerSecToNative(double fps) {
		if (inchesPerRotation == null) {
			return null;
		}
		return RPSToEncoder(fps / (inchesPerRotation / 12.));
	}

	/**
	 * Convert a velocity from encoder units to feet per second. Note this DOES account for post-encoder gearing.
	 *
	 * @param nativeUnits A velocity in either native units or RPS, depending on the type of encoder.
	 * @return That velocity in feet per second, or null if no encoder CPR or inches per rotation was given.
	 */
	@Nullable
	public Double nativeToFeetPerSec(double nativeUnits) {
		Double RPS = encoderToRPS(nativeUnits);
		if (inchesPerRotation == null || RPS == null) {
			return null;
		}
		return RPS * inchesPerRotation / 12.;
	}

	/**
	 * @return The CANTalon this is a wrapper on.
	 */
	@NotNull
	public CANTalon getCanTalon() {
		return canTalon;
	}

	/**
	 * Set the velocity for the motor to go at.
	 *
	 * @param velocity the desired velocity, on [-1, 1].
	 */
	@Override
	public void setVelocity(double velocity) {
		if (maxSpeed != null) {
			setSpeed(velocity*maxSpeed);
		} else {
			setPercentVbus(velocity);
		}
	}

	/**
	 * Enables the motor, if applicable.
	 */
	@Override
	public void enable() {
		canTalon.enable();
	}

	/**
	 * Disables the motor, if applicable.
	 */
	@Override
	public void disable() {
		canTalon.disable();
	}

	/**
	 * Set the velocity scaled to a given gear's max velocity. Used mostly when autoshifting.
	 *
	 * @param velocity The velocity to go at, from [-1, 1], where 1 is the max speed of the given gear.
	 * @param gear The gear to use the max speed from to scale the velocity.
	 */
	public void setGearScaledVelocity(double velocity, @NotNull Shiftable.gear gear){
		if (maxSpeed == null){
			setPercentVbus(velocity);
		} else if (gear.equals(Shiftable.gear.HIGH)) {
			setVelocity(velocity * maxSpeedHigh);
		} else {
			setVelocity(velocity*maxSpeedLow);
		}
	}

	/**
	 * An object representing a slave {@link CANTalon} for use in the map.
	 */
	@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
	private static class SlaveTalon {

		/**
		 * The port number of this Talon.
		 */
		private final int port;

		/**
		 * Whether this Talon is inverted compared to its master.
		 */
		private final boolean inverted;

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
		 * @return The port number of this Talon.
		 */
		public int getPort() {
			return port;
		}

		/**
		 * @return true if this Talon is inverted compared to its master, false otherwise.
		 */
		public boolean isInverted() {
			return inverted;
		}
	}
}
