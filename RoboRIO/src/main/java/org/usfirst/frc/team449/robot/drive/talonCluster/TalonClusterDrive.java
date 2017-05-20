package org.usfirst.frc.team449.robot.drive.talonCluster;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Command;
import maps.org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRXMap;
import maps.org.usfirst.frc.team449.robot.util.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;
import org.usfirst.frc.team449.robot.util.BufferTimer;
import org.usfirst.frc.team449.robot.util.Loggable;
import org.usfirst.frc.team449.robot.util.Logger;


/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side.
 */
public class TalonClusterDrive extends DriveSubsystem implements NavxSubsystem, UnidirectionalDrive, ShiftingDrive, Loggable {

	/**
	 * Joystick scaling constant. Joystick output is scaled by this before being handed to the PID loop to give the
	 * loop space to compensate.
	 */
	private double PID_SCALE;
	/**
	 * Right master Talon
	 */
	public RotPerSecCANTalonSRX rightMaster;
	/**
	 * Left master Talon
	 */
	public RotPerSecCANTalonSRX leftMaster;
	/**
	 * The NavX gyro
	 */
	public AHRS navx;
	/**
	 * The PIDAngleCommand constants for turning to an angle with the NavX
	 */
	public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID turnPID;
	/**
	 * The PIDAngleCommand constants for using the NavX to drive straight
	 */
	public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID straightPID;
	/**
	 * The oi used to drive the robot
	 */
	public OI2017ArcadeGamepad oi;

	/**
	 * The solenoid that shifts between gears
	 */
	public DoubleSolenoid shifter;
	/**
	 * Whether or not to use the NavX for driving straight
	 */
	private boolean overrideNavX;

	public boolean getOverrideAutoshift() {
		return overrideAutoshift;
	}

	public void setOverrideAutoshift(boolean override) {
		this.overrideAutoshift = override;
	}

	/**
	 * Whether not to override auto shifting
	 */
	private boolean overrideAutoshift;
	/**
	 * The forward velocity setpoint (on a 0-1 scale) below which we stay in low gear
	 */
	private double upshiftFwdThresh;
	/**
	 * The time we last upshifted (milliseconds)
	 */
	private long timeLastUpshifted;
	/**
	 * The time we last downshifted (milliseconds)
	 */
	private long timeLastDownshifted;
	/**
	 * What gear we're in
	 */
	private ShiftingDrive.gear currentGear;

	/**
	 * The speed setpoint at the upshift break
	 */
	private double upshiftSpeed;

	/**
	 * The speed setpoint at the downshift break
	 */
	private double downshiftSpeed;

	/**
	 * The robot isn't eligible to shift again for this many milliseconds after upshifting.
	 */
	private long cooldownAfterUpshift;

	/**
	 * The robot isn't eligible to shift again for this many milliseconds after downshifting.
	 */
	private long cooldownAfterDownshift;

	private BufferTimer upshiftBufferTimer, downshiftBufferTimer;

	/**
	 * Construct a TalonClusterDrive
	 *
	 * @param map config map
	 * @param oi  OI to read throttle from
	 */
	public TalonClusterDrive(maps.org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDriveMap
			                         .TalonClusterDrive map, OI2017ArcadeGamepad oi, gear startingGear) {
		super(map.getDrive());
		PID_SCALE = map.getPIDScale();

		this.map = map;
		this.oi = oi;
		navx = new AHRS(SPI.Port.kMXP);
		turnPID = map.getTurnPID();
		straightPID = map.getStraightPID();
		upshiftBufferTimer = new BufferTimer(map.getDelayAfterUpshiftConditionsMet());
		downshiftBufferTimer = new BufferTimer(map.getDelayAfterDownshiftConditionsMet());
		cooldownAfterDownshift = (long) (map.getCooldownAfterDownshift()*1000.);
		cooldownAfterUpshift = (long) (map.getCooldownAfterUpshift()*1000.);
		upshiftFwdThresh = map.getUpshiftFwdThreshold();
		upshiftSpeed = map.getUpshiftSpeed();
		downshiftSpeed = map.getDownshiftSpeed();
		currentGear = startingGear;

		// Initialize shifting constants, assuming robot is stationary.
		overrideAutoshift = false;
		timeLastUpshifted = 0;
		timeLastDownshifted = 0;

		// If the map has the shifting piston, instantiate it.
		if (map.hasShifter()) {
			this.shifter = new DoubleSolenoid(map.getModuleNumber(), map.getShifter().getForward(), map.getShifter()
					.getReverse());
		}

		// Initialize max

		// Initialize master talons
		rightMaster = new RotPerSecCANTalonSRX(map.getRightMaster());
		leftMaster = new RotPerSecCANTalonSRX(map.getLeftMaster());

		// Initialize slave talons.
		for (RotPerSecCANTalonSRXMap.RotPerSecCANTalonSRX talon : map.getRightSlaveList()) {
			RotPerSecCANTalonSRX talonObject = new RotPerSecCANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getRightMaster().getPort());
		}
		for (RotPerSecCANTalonSRXMap.RotPerSecCANTalonSRX talon : map.getLeftSlaveList()) {
			RotPerSecCANTalonSRX talonObject = new RotPerSecCANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getLeftMaster().getPort());
		}
	}

	/**
	 * Simple helper function for clipping output to the -1 to 1 scale.
	 *
	 * @param in The number to be processed.
	 * @return That number, clipped to 1 if it's greater than 1 or clipped to -1 if it's less than -1.
	 */
	private static double clipToOne(double in) {
		return Math.min(Math.max(in, -1), 1);
	}

	/**
	 * Sets the left and right wheel speeds as a percent of max voltage, not nearly as precise as PID.
	 *
	 * @param left  The left voltage throttle, [-1, 1]
	 * @param right The right voltage throttle, [-1, 1]
	 */
	public void setVBusThrottle(double left, double right) {
		//Set voltage mode throttles
		leftMaster.setPercentVbus(left);
		rightMaster.setPercentVbus(-right); //This is negative so PID doesn't have to be. Future people, if your robot goes in circles in voltage mode, this may be why.
	}

	/**
	 * Sets left and right wheel PID velocity setpoint as a percent of max setpoint
	 *
	 * @param left  The left PID velocity setpoint as a percent [-1, 1]
	 * @param right The right PID velocity setpoint as a percent [-1, 1]
	 */
	private void setPIDThrottle(double left, double right) {
		//If we're not shifting, scale by the max speed in the current gear
		if (overrideAutoshift || oi.getFwd() == 0) {
			leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeed()));
			rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeed()));
		}
		//If we are shifting, scale by the high gear max speed to make acceleration smoother and faster.
		else {
			leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeedHG()));
			rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeedHG()));
		}
	}

	/**
	 * Set the output of each side of the drive.
	 * @param left The output for the left side of the drive, from [-1, 1]
	 * @param right the output for the right side of the drive, from [-1, 1]
	 */
	public void setOutput(double left, double right) {
		//Clip to one to avoid anything strange.
		setPIDThrottle(clipToOne(left), clipToOne(right));
//		setVBusThrottle(left, right);
	}

	/**
	 * Completely stop the robot by setting the voltage to each side to be 0.
	 */
	@Override
	public void fullStop() {
		setVBusThrottle(0, 0);
	}

	/**
	 * Stuff run on first enable
	 * Reset startTime, turn on navX control, and start UnidirectionalNavXArcadeDrive
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing, the default command gets set with setDefaultCommandManual
	}

	public void setDefaultCommandManual(Command defaultCommand) {
		setDefaultCommand(defaultCommand);
	}

	/**
	 * Get the robot's heading using the navX
	 *
	 * @return robot heading (degrees) [-180, 180]
	 */
	public double getGyroOutput() {
		return navx.pidGet();
	}

	@Override
	public void setOverrideNavX(boolean override) {
		overrideNavX = override;
	}

	/**
	 * Shift as appropriate
	 *
	 * @param gear Which gear to shift to.
	 */
	public void setGear(gear gear) {
		//If we have a shifter on the robot
		if (shifter != null) {
			//If we want to downshift
			if (gear == ShiftingDrive.gear.LOW) {
				//Physically shift gears
				shifter.set(DoubleSolenoid.Value.kForward);
				//Switch the PID constants
				rightMaster.switchToLowGear();
				leftMaster.switchToLowGear();
				timeLastDownshifted = Robot.currentTimeMillis();
			} else {
				//Physically shift gears
				shifter.set(DoubleSolenoid.Value.kReverse);
				//Switch the PID constants
				rightMaster.switchToHighGear();
				leftMaster.switchToHighGear();
				timeLastUpshifted = Robot.currentTimeMillis();
			}
			//Set logging var
			currentGear = gear;
		} else {
			//Warn the user if they try to shift but didn't define a shifting piston.
			Logger.addEvent("You're trying to shift gears, but your drive doesn't have a shifter.", this.getClass());
		}
	}

	/**
	 * @return whether the robot is in low gear
	 */
	public gear getGear() {
		return currentGear;
	}

	/**
	 * @return whether the robot should downshift
	 */
	private boolean shouldDownshift() {
		//We should shift if we're going slower than the downshift speed
		boolean okToShift = Math.max(Math.abs(leftMaster.getSpeed()), Math.abs(rightMaster.getSpeed())) < downshiftSpeed;
		//Or if we're just turning in place.
		okToShift = okToShift || (oi.getFwd() == 0 && oi.getRot() != 0);
		//Or commanding a low speed.
		okToShift = okToShift || (Math.abs(oi.getFwd()) < upshiftFwdThresh);
		//But we can only shift if we're out of the cooldown period.
		okToShift = okToShift && Robot.currentTimeMillis() - timeLastUpshifted > cooldownAfterUpshift;
		//And there's no need to downshift if we're already in low gear.
		okToShift = okToShift && currentGear == gear.HIGH;
		//And we don't want to shift if autoshifting is turned off.
		okToShift = okToShift && !overrideAutoshift;

		return downshiftBufferTimer.get(okToShift);
	}

	/**
	 * @return whether the robot should upshift
	 */
	private boolean shouldUpshift() {
		//We should shift if we're going faster than the upshift speed...
		boolean okToShift = Math.min(Math.abs(leftMaster.getSpeed()), Math.abs(rightMaster.getSpeed())) > upshiftSpeed;
		//AND the driver's trying to go forward fast.
		okToShift = okToShift && Math.abs(oi.getFwd()) > upshiftFwdThresh;
		//But we can only shift if we're out of the cooldown period.
		okToShift = okToShift && Robot.currentTimeMillis() - timeLastDownshifted > cooldownAfterDownshift;
		//And there's no need to upshift if we're already in high gear.
		okToShift = okToShift && currentGear == gear.LOW;
		//And we don't want to shift if autoshifting is turned off.
		okToShift = okToShift && !overrideAutoshift;

		return upshiftBufferTimer.get(okToShift);
	}

	/**
	 * Shift if necessary
	 */
	public void autoshift() {
		if (shouldUpshift()) {
			//Upshift if we should
			setGear(gear.HIGH);
		} else if (shouldDownshift()) {
			//Downshift if we should
			setGear(gear.LOW);
		}
	}

	public boolean getOverrideNavX(){
		return overrideNavX;
	}

	@Override
	public AHRS getNavX() {
		return navx;
	}

	@Override
	public String getHeader() {
		return "left_vel," +
				"right_vel," +
				"left_setpoint," +
				"right_setpoint," +
				"left_current," +
				"right_current," +
				"left_voltage," +
				"right_voltage";
	}

	@Override
	public Object[] getData() {
		return new Object[]{leftMaster.getSpeed(),
				rightMaster.getSpeed(),
				leftMaster.getSetpoint(),
				rightMaster.getSetpoint(),
				leftMaster.canTalon.getOutputCurrent(),
				rightMaster.canTalon.getOutputCurrent(),
				leftMaster.canTalon.getOutputVoltage(),
				rightMaster.canTalon.getOutputVoltage()};
	}

	@Override
	public String getName(){
		return "Drive";
	}
}
