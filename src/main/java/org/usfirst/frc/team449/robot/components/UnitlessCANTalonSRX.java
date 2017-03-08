package org.usfirst.frc.team449.robot.components;

import com.ctre.CANTalon;
import maps.org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRXMap;

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
	public double encoderCPR;

	protected CANTalon.FeedbackDevice feedbackDevice;

	private UnitlessCANTalonSRXMap.UnitlessCANTalonSRX map;

	/**
	 * Construct the CANTalonSRX from its map object
	 *
	 * @param map CANTalonSRX map object
	 */
	public UnitlessCANTalonSRX(maps.org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRXMap.UnitlessCANTalonSRX map) {
		// Configure stuff
		this.map = map;
		canTalon = new CANTalon(map.getPort());
		maxSpeed = map.getMaxSpeedHg();
		encoderCPR = map.getEncoderCPR();
		canTalon.setFeedbackDevice(CANTalon.FeedbackDevice.valueOf(map.getFeedbackDevice().getNumber()));
		feedbackDevice = CANTalon.FeedbackDevice.valueOf(map.getFeedbackDevice().getNumber());
		canTalon.reverseSensor(map.getReverseSensor());
		canTalon.reverseOutput(map.getReverseOutput());
		canTalon.setInverted(map.getIsInverted());
		canTalon.configNominalOutputVoltage(+map.getNominalOutVoltage(), -map.getNominalOutVoltage());
		canTalon.configPeakOutputVoltage(+map.getPeakOutVoltage(), -map.getPeakOutVoltage());

		setPIDF(map.getKPHg(), map.getKIHg(), map.getKDHg(), maxSpeed, 0, 0, 0);
		if (map.hasKPMp()) {
			setPIDF(map.getKPMp(), map.getKIMp(), map.getKDMp(), map.getMaxSpeedHg(), 0, 0, 1);
		}
		canTalon.setProfile(map.getProfile());

		// Configure more stuff
		canTalon.ConfigFwdLimitSwitchNormallyOpen(map.getFwdLimNormOpen());
		canTalon.ConfigRevLimitSwitchNormallyOpen(map.getRevLimNormOpen());
		canTalon.enableLimitSwitch(map.getFwdLimEnabled(), map.getRevLimEnabled());
		canTalon.enableForwardSoftLimit(map.getFwdSoftLimEnabled());
		canTalon.setForwardSoftLimit(map.getFwdSoftLimVal());
		canTalon.enableReverseSoftLimit(map.getRevSoftLimEnabled());
		canTalon.setReverseSoftLimit(map.getRevSoftLimVal());
		canTalon.enableBrakeMode(map.getBrakeMode());
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

	private void setPIDF(double p, double i, double d, double maxSpeed, int iZone, double closeLoopRampRate, int profile){
		this.canTalon.setPID(p, i, d, 1023/RPStoNative(maxSpeed), iZone, closeLoopRampRate, profile);
	}

	public void switchToHighGear(){
		maxSpeed = map.getMaxSpeedHg();
		setPIDF(map.getKPHg(), map.getKIHg(), map.getKDHg(), maxSpeed, 0, 0, 0);
	}

	public void switchToLowGear(){
		if (map.hasKPLg()) {
			maxSpeed = map.getMaxSpeedLg();
			setPIDF(map.getKPLg(), map.getKILg(), map.getKDLg(), maxSpeed, 0, 0, 0);
		} else {
			System.out.println("You're trying to switch your PIDF constants to low gear, but you don't have low gear constants.");
		}
	}

	public boolean getInverted() {
		return false;
	}

	public void setInverted(boolean b) {
	}

	public Double getMaxSpeed() {
		return maxSpeed;
	}

	public double RPStoNative(double RPS) {
		return (RPS / 10) * (encoderCPR * 4); //4 edges per count, and 10 100ms per second.
	}

	public double nativeToRPS(double nat) {
		return (nat / (encoderCPR * 4)) * 10; //4 edges per count, and 10 100ms per second.
	}

	public double getSpeed() {
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative || feedbackDevice == CANTalon
				.FeedbackDevice.CtreMagEncoder_Absolute){
			return canTalon.getSpeed()*60;
		}
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
		canTalon.changeControlMode(CANTalon.TalonControlMode.Speed);
		if (feedbackDevice == CANTalon.FeedbackDevice.CtreMagEncoder_Relative || feedbackDevice == CANTalon
				.FeedbackDevice.CtreMagEncoder_Absolute)
			canTalon.set(velocitySp * 60); // 60 converts from RPS to RPM, TODO figure out where the 60 should
			// actually go
		else
			canTalon.set(RPStoNative(velocitySp));
	}

	/**
	 * Set which slot the Talon reads the PID gains from
	 *
	 * @param slot gains slot (0 or 1)
	 * @throws Exception if the specified slot isn't 0 or 1
	 */
	@Deprecated
	public void setPSlot(int slot) throws Exception {
		canTalon.setProfile(slot);
	}

	public double getError(){
		return nativeToRPS(canTalon.getError());
	}

	public double getMaxSpeedHG(){
		return map.getMaxSpeedHg();
	}
}
