package org.usfirst.frc.team449.robot.drive.talonCluster;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.components.MappedDoubleSolenoid;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePID;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;
import org.usfirst.frc.team449.robot.util.BufferTimer;
import org.usfirst.frc.team449.robot.util.CANTalonMPHandler;
import org.usfirst.frc.team449.robot.util.Logger;


/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side and a high and low gear.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class ShiftingTalonClusterDrive extends TalonClusterDrive implements ShiftingDrive {

	/**
	 * The solenoid that shifts between gears
	 */
	private DoubleSolenoid shifter;

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
	private gear currentGear;

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
	 * BufferTimers for shifting that make it so all the other conditions to shift must be met for some amount of time
	 * before shifting actually happens.
	 */
	private BufferTimer upshiftBufferTimer, downshiftBufferTimer;

	/**
	 * The OI controlling this drive.
	 */
	private ArcadeOI oi;

	/**
	 * Default constructor.
	 * @param turnPID The angular PID for turning in place.
	 * @param straightPID The angular PID for driving straight.
	 * @param leftMaster The master talon on the left side of the drive.
	 * @param rightMaster The master talon on the right side of the drive.
	 * @param MPHandler The motion profile handler that runs this drive's motion profiles.
	 * @param PIDScale The amount to scale the output to the PID loop by. Defaults to 1.
	 * @param oi The ArcadeOI used to control this drive.
	 * @param upshiftSpeed The minimum speed both sides the drive must be going at to shift to high gear.
	 * @param downshiftSpeed The maximum speed both sides must be going at to shift to low gear.
	 * @param shifter The piston that shifts between gears.
	 * @param delayAfterUpshiftConditionsMet How long, in seconds, the conditions to upshift have to be met for before
	 *                                       upshifting happens. Defaults to 0.
	 * @param delayAfterDownshiftConditionsMet How long, in seconds, the conditions to downshift have to be met for
	 *                                            before downshifting happens. Defaults to 0.
	 * @param cooldownAfterDownshift The minimum time, in seconds, between downshifting and then upshifting again.
	 *                              Defaults to 0.
	 * @param cooldownAfterUpshift The minimum time, in seconds, between upshifting and then downshifting again.
	 *                              Defaults to 0.
	 * @param upshiftFwdThresh The minimum amount the forward joystick must be pushed forward in order to upshift, on
	 *                            [0, 1]. Defaults to 0.
	 * @param startingGear The gear the drive starts in. Defaults to low.
	 */
	@JsonCreator
	public ShiftingTalonClusterDrive(@JsonProperty(required = true) ToleranceBufferAnglePID turnPID,
	                                 @JsonProperty(required = true) ToleranceBufferAnglePID straightPID,
	                                 @JsonProperty(required = true) RotPerSecCANTalonSRX leftMaster,
	                                 @JsonProperty(required = true) RotPerSecCANTalonSRX rightMaster,
	                                 @JsonProperty(required = true) CANTalonMPHandler MPHandler,
	                                 Double PIDScale,
	                                 @JsonProperty(required = true) ArcadeOI oi,
	                                 @JsonProperty(required = true) double upshiftSpeed,
	                                 @JsonProperty(required = true) double downshiftSpeed,
	                                 @JsonProperty(required = true) MappedDoubleSolenoid shifter,
	                                 double delayAfterUpshiftConditionsMet,
	                                 double delayAfterDownshiftConditionsMet,
	                                 double cooldownAfterDownshift,
	                                 double cooldownAfterUpshift,
	                                 double upshiftFwdThresh,
	                                 gear startingGear) {
		super(turnPID, straightPID, leftMaster, rightMaster, MPHandler, PIDScale);
		//Initialize stuff
		this.oi = oi;
		upshiftBufferTimer = new BufferTimer(delayAfterUpshiftConditionsMet);
		downshiftBufferTimer = new BufferTimer(delayAfterDownshiftConditionsMet);
		this.cooldownAfterDownshift = (long) (cooldownAfterDownshift * 1000.);
		this.cooldownAfterUpshift = (long) (cooldownAfterUpshift * 1000.);
		this.upshiftFwdThresh = upshiftFwdThresh;
		this.upshiftSpeed = upshiftSpeed;
		this.downshiftSpeed = downshiftSpeed;
		this.shifter = shifter;

		//Default to low
		if (startingGear == null){
			startingGear = gear.LOW;
		}
		currentGear = startingGear;

		// Initialize shifting constants, assuming robot is stationary.
		overrideAutoshift = false;
		timeLastUpshifted = 0;
		timeLastDownshifted = 0;
	}

	/**
	 * A getter for whether we're currently overriding autoshifting.
	 *
	 * @return true if overriding, false otherwise.
	 */
	@Override
	public boolean getOverrideAutoshift() {
		return overrideAutoshift;
	}

	/**
	 * A setter for overriding the autoshifting.
	 *
	 * @param override Whether or not to override autoshifting.
	 */
	@Override
	public void setOverrideAutoshift(boolean override) {
		this.overrideAutoshift = override;
	}

	/**
	 * Sets left and right wheel PID velocity setpoint as a percent of max setpoint
	 *
	 * @param left  The left PID velocity setpoint as a percent [-1, 1]
	 * @param right The right PID velocity setpoint as a percent [-1, 1]
	 */
	@Override
	protected void setPIDThrottle(double left, double right) {
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
}
