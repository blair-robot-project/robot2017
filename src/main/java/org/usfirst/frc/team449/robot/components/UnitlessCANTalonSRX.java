package org.usfirst.frc.team449.robot.components;

import com.ctre.CANTalon;

/**
 * Component wrapper on CTRE CAN Talon SRX {@link CANTalon}, with unit conversions to/from RPS built in.
 */
public class UnitlessCANTalonSRX extends Component {

	/**
	 * The CTRE CAN Talon SRX that this class is a wrapper on
	 */
	public CANTalon canTalon;

	/**
	 * The maximum speed of the motor, in human units.
	 */
	protected double maxSpeed;

	/**
	 * The counts per rotation of the encoder being used.
	 */
	protected double encoderCPR;

	protected CANTalon.FeedbackDevice feedbackDevice;

	/**
	 * kP of the internal PID loop
	 */
	protected double kP;
	/**
	 * kI of the internal PID loop
	 */
	protected double kI;
	/**
	 * kD of the internal PID loop
	 */
	protected double kD;
	/**
	 * kF of the interal PID loop
	 */
	protected double kF;

	/**
	 * Construct the CANTalonSRX from its map object
	 *
	 * @param m CANTalonSRX map object
	 */
	public UnitlessCANTalonSRX(maps.org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRXMap.UnitlessCANTalonSRX m) {
		// Configure stuff
		canTalon = new CANTalon(m.getPort());
		maxSpeed = m.getMaxSpeed();
		encoderCPR = m.getEncoderCPR();
		canTalon.setFeedbackDevice(CANTalon.FeedbackDevice.valueOf(m.getFeedbackDevice().getNumber()));
		feedbackDevice = CANTalon.FeedbackDevice.valueOf(m.getFeedbackDevice().getNumber());
		canTalon.reverseSensor(m.getReverseSensor());
		canTalon.reverseOutput(m.getReverseOutput());
		canTalon.setInverted(m.getIsInverted());
		canTalon.configNominalOutputVoltage
				(+m.getNominalOutVoltage(), -m.getNominalOutVoltage());
		canTalon.configPeakOutputVoltage(+m.getPeakOutVoltage(),
				-m.getPeakOutVoltage());
		canTalon.setProfile(m.getProfile());

		/*
		 * Read the PIDF constants from the map, then call setPIDF to scale the stuff in the map as desired to get to
		 * native units, appropriates or whatever the hell the controller expect, then set the PIDF slots of the
		 * hardware and choose the slot
		 */
		kP = m.getKP();
		kI = m.getKI();
		kD = m.getKD();

		canTalon.setPID(m.getKP(), m.getKI(), m.getKD(), 1023 / RPStoNative(m.getMaxSpeed()), 0, 0, 0);
		canTalon.setProfile(0);

		// Configure more stuff
		canTalon.ConfigFwdLimitSwitchNormallyOpen(m.getFwdLimNormOpen());
		canTalon.ConfigRevLimitSwitchNormallyOpen(m.getRevLimNormOpen());
		canTalon.enableLimitSwitch(m.getFwdLimEnabled(), m.getRevLimEnabled());
		canTalon.enableForwardSoftLimit(m.getFwdSoftLimEnabled());
		canTalon.setForwardSoftLimit(m.getFwdSoftLimVal());
		canTalon.enableReverseSoftLimit(m.getRevSoftLimEnabled());
		canTalon.setReverseSoftLimit(m.getRevSoftLimVal());
		canTalon.enableBrakeMode(m.getBrakeMode());
	}

	/**
	 * Give a PercentVbus setpoint (set to PercentVbus mode and set)
	 *
	 * @param percentVbus percent of total voltage (between -1.0 and +1.0)
	 */
	public void setPercentVbus(double percentVbus) {
		if (Math.abs(percentVbus) > 1.0) {
			System.out.println("WARNING: YOU ARE CLIPPING MAX PERCENT VBUS AT " + percentVbus);
			percentVbus = Math.signum(percentVbus);
		}
		canTalon.changeControlMode(CANTalon.TalonControlMode.PercentVbus);
		canTalon.set(percentVbus);
	}

	/**
	 * Give a position closed loop setpoint
	 * TODO: figure out units and warning clip to input range
	 *
	 * @param positionSp position setpoint
	 */
	public void setPosition(double positionSp) {
		canTalon.changeControlMode(CANTalon.TalonControlMode.Position);
		canTalon.set(positionSp);
	}

	public boolean getInverted() {
		return false;
	}

	public void setInverted(boolean b) {
	}

	public Double getMaxSpeed() {
		if (maxSpeed == 0.0)
			return null;
		return maxSpeed;
	}

	public double RPStoNative(double RPS) {
		return (RPS / 10) * (encoderCPR * 4); //4 edges per count, and 10 100ms per second.
	}

	public double nativeToRPS(double nat) {
		return (nat / (encoderCPR * 4)) * 10; //4 edges per count, and 10 100ms per second.
	}

	public double getSpeed() {
		return nativeToRPS(canTalon.getEncVelocity());
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
		canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative || feedbackDevice == CANTalon
				.FeedbackDevice.CtreMagEncoder_Absolute)
			canTalon.set(velocitySp * 60); // 60 converts from RPS to RPM, TODO figure out where the 60 should actually go
		else
			canTalon.set(RPStoNative(velocitySp));
	}
}
