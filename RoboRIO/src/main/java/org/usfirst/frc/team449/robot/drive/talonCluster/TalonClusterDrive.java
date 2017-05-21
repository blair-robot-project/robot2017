package org.usfirst.frc.team449.robot.drive.talonCluster;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Command;
import maps.org.usfirst.frc.team449.robot.util.MotionProfileMap;
import maps.org.usfirst.frc.team449.robot.util.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;
import org.usfirst.frc.team449.robot.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side.
 */
public class TalonClusterDrive extends DriveSubsystem implements NavxSubsystem, UnidirectionalDrive, ShiftingDrive, Loggable, TwoSideMPSubsystem {

	/**
	 * The period of the thread that moves points from the API-level MP buffer to the low-level one.
	 */
	private final double MP_UPDATE_RATE;

	/**
	 * The PIDAngleCommand constants for turning to an angle with the NavX
	 */
	public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID turnPID;

	/**
	 * The PIDAngleCommand constants for using the NavX to drive straight
	 */
	public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID straightPID;

	/**
	 * Joystick scaling constant. Joystick output is scaled by this before being handed to the PID loop to give the
	 * loop space to compensate.
	 */
	private double PID_SCALE;

	/**
	 * Right master Talon
	 */
	private RotPerSecCANTalonSRX rightMaster;

	/**
	 * Left master Talon
	 */
	private RotPerSecCANTalonSRX leftMaster;

	/**
	 * The NavX gyro
	 */
	private AHRS navx;

	/**
	 * The oi used to drive the robot
	 */
	private OI2017ArcadeGamepad oi;

	/**
	 * The solenoid that shifts between gears
	 */
	private DoubleSolenoid shifter;

	/**
	 * Whether or not to use the NavX for driving straight
	 */
	private boolean overrideNavX;

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

	/**
	 * BufferTimers for shifting that make it so all the other conditions to shift must be met for some amount of time before shifting actually happens.
	 */
	private BufferTimer upshiftBufferTimer, downshiftBufferTimer;

	/**
	 * The minimum number of points that must be in the bottom-level Motion Profile before we start running the profile.
	 */
	private int minPointsInBtmMPBuffer;

	/**
	 * The Notifier that moves points from the API-level MP buffer to the low-level one.
	 */
	private Notifier MPNotifier;

	/**
	 * The talons on this subsystem that we want to run motion profiles on.
	 */
	private List<CANTalon> MPTalons;

	/**
	 * Construct a TalonClusterDrive
	 *
	 * @param map config map
	 * @param oi  OI to read throttle from
	 */
	public TalonClusterDrive(maps.org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDriveMap
			                         .TalonClusterDrive map, OI2017ArcadeGamepad oi, gear startingGear) {
		super(map.getDrive());
		//Initialize stuff directly from the map.
		this.map = map;
		this.oi = oi;
		PID_SCALE = map.getPIDScale();
		turnPID = map.getTurnPID();
		straightPID = map.getStraightPID();
		upshiftBufferTimer = new BufferTimer(map.getDelayAfterUpshiftConditionsMet());
		downshiftBufferTimer = new BufferTimer(map.getDelayAfterDownshiftConditionsMet());
		cooldownAfterDownshift = (long) (map.getCooldownAfterDownshift() * 1000.);
		cooldownAfterUpshift = (long) (map.getCooldownAfterUpshift() * 1000.);
		upshiftFwdThresh = map.getUpshiftFwdThreshold();
		upshiftSpeed = map.getUpshiftSpeed();
		downshiftSpeed = map.getDownshiftSpeed();
		currentGear = startingGear;
		MP_UPDATE_RATE = map.getMPUpdateRateSecs();
		minPointsInBtmMPBuffer = map.getMinPointsInBottomMPBuffer();
		rightMaster = new RotPerSecCANTalonSRX(map.getRightMaster());
		leftMaster = new RotPerSecCANTalonSRX(map.getLeftMaster());

		//We only ever use a NavX on the SPI port because the other ports don't work.
		navx = new AHRS(SPI.Port.kMXP);

		// Initialize shifting constants, assuming robot is stationary.
		overrideAutoshift = false;
		timeLastUpshifted = 0;
		timeLastDownshifted = 0;

		// If the map has the shifting piston, instantiate it.
		if (map.hasShifter()) {
			this.shifter = new MappedDoubleSolenoid(map.getShifter());
		}

		//Add the masters to the list of Talons to use for MP
		MPTalons = new ArrayList<>();
		MPTalons.add(leftMaster.canTalon);
		MPTalons.add(rightMaster.canTalon);

		//Set up the MPNotifier to run an MPUpdaterProcess containing the left and right master talons.
		MPUpdaterProcess updater = new MPUpdaterProcess();
		updater.addTalon(rightMaster.canTalon);
		updater.addTalon(leftMaster.canTalon);
		MPNotifier = new Notifier(updater);
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
	 * A getter for whether we're currently overriding autoshifting.
	 * @return true if overriding, false otherwise.
	 */
	@Override
	public boolean getOverrideAutoshift() {
		return overrideAutoshift;
	}

	/**
	 * A setter for overriding the autoshifting.
	 * @param override Whether or not to override autoshifting.
	 */
	@Override
	public void setOverrideAutoshift(boolean override) {
		this.overrideAutoshift = override;
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
	 *
	 * @param left  The output for the left side of the drive, from [-1, 1]
	 * @param right the output for the right side of the drive, from [-1, 1]
	 */
	@Override
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
	 * If this drive uses motors that can be disabled, enable them.
	 */
	@Override
	public void enableMotors() {
		leftMaster.canTalon.enable();
		rightMaster.canTalon.enable();
	}

	/**
	 * Stuff run on first enable.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing, the default command gets set with setDefaultCommandManual
	}

	/**
	 * Set the default command. Done here instead of in initDefaultCommand so we don't have a defaultCommand during auto.
	 * @param defaultCommand The command to have run by default. Must require this subsystem.
	 */
	public void setDefaultCommandManual(Command defaultCommand) {
		setDefaultCommand(defaultCommand);
	}

	/**
	 * Get the robot's heading using the navX
	 *
	 * @return robot heading (degrees) [-180, 180]
	 */
	@Override
	public double getGyroOutput() {
		return navx.pidGet();
	}

	/**
	 * @return The gear this subsystem is currently in.
	 */
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
				//Record the current time
				timeLastDownshifted = Robot.currentTimeMillis();
			} else {
				//Physically shift gears
				shifter.set(DoubleSolenoid.Value.kReverse);
				//Switch the PID constants
				rightMaster.switchToHighGear();
				leftMaster.switchToHighGear();
				//Record the current time.
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

		//We use a BufferTimer so we only shift if the conditions are met for a specific continuous interval.
		// This avoids brief blips causing shifting.
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

		//We use a BufferTimer so we only shift if the conditions are met for a specific continuous interval.
		// This avoids brief blips causing shifting.
		return upshiftBufferTimer.get(okToShift);
	}

	/**
	 * Check if we should autoshift, then, if so, shift.
	 */
	@Override
	public void autoshift() {
		if (shouldUpshift()) {
			//Upshift if we should
			setGear(gear.HIGH);
		} else if (shouldDownshift()) {
			//Downshift if we should
			setGear(gear.LOW);
		}
	}

	/**
	 * Get whether this subsystem's NavX is currently being overriden.
	 * @return true if the NavX is overriden, false otherwise.
	 */
	@Override
	public boolean getOverrideNavX() {
		return overrideNavX;
	}

	/**
	 * Set whether or not to override this subsystem's NavX.
	 * @param override true to override, false otherwise.
	 */
	@Override
	public void setOverrideNavX(boolean override) {
		overrideNavX = override;
	}

	/**
	 * Get the NavX this subsystem uses.
	 * @return An AHRS object representing this subsystem's NavX.
	 */
	@Override
	public AHRS getNavX() {
		return navx;
	}

	/**
	 * Get the headers for the data this subsystem logs every loop.
	 * @return A string consisting of N comma-separated labels for data, where N is the length of the Object[] returned by getData().
	 */
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

	/**
	 * Get the data this subsystem logs every loop.
	 * @return An N-length array of Objects, where N is the number of labels given by getHeader.
	 */
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

	/**
	 * Get the name of this object.
	 * @return A string that will identify this object in the log file.
	 */
	@Override
	public String getName() {
		return "Drive";
	}

	/**
	 * Loads a profile into the MP buffer.
	 *
	 * @param profile The profile to be loaded.
	 */
	@Override
	public void loadMotionProfile(MotionProfileData profile) {
		//Stop loading points from the API-level buffer into the low-level one.
		MPNotifier.stop();
		//Fill the API-level buffer with the points from the given profile.
		MPLoader.loadTopLevel(profile, leftMaster);
		MPLoader.loadTopLevel(profile, rightMaster);
		//Resume loading points from the API-level buffer into the low-level one.
		MPNotifier.startPeriodic(MP_UPDATE_RATE);
	}

	/**
	 * Get the Talons in this subsystem to run the MP on.
	 *
	 * @return a List of Talons with encoders attached (e.g. master talons)
	 */
	@Override
	public List<CANTalon> getTalons() {
		return MPTalons;
	}

	/**
	 * Get the minimum number of points that can be in the bottom-level motion profile buffer before we start driving
	 * the profile
	 *
	 * @return an integer from [0, 128]
	 */
	@Override
	public int getMinPointsInBtmBuffer() {
		return minPointsInBtmMPBuffer;
	}

	/**
	 * Stops any MP-related threads currently running. Normally called at the start of teleop.
	 */
	@Override
	public void stopMPProcesses() {
		MPNotifier.stop();
	}

	/**
	 * Loads given profiles into the left and right sides of the drive.
	 *
	 * @param left  The profile to load into the left side.
	 * @param right The profile to load into the right side.
	 */
	@Override
	public void loadMotionProfile(MotionProfileData left, MotionProfileData right) {
		//Stop loading points from the API-level buffer into the low-level one.
		MPNotifier.stop();
		//Fill the API-level buffer with the points from the given profile.
		MPLoader.loadTopLevel(left, leftMaster);
		MPLoader.loadTopLevel(right, rightMaster);
		//Resume loading points from the API-level buffer into the low-level one.
		MPNotifier.startPeriodic(MP_UPDATE_RATE);
	}
}
