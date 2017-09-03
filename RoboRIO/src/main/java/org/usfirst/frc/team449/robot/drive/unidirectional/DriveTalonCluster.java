package org.usfirst.frc.team449.robot.drive.unidirectional;

import com.fasterxml.jackson.annotation.*;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.command.Command;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.CANTalonMPComponent;
import org.usfirst.frc.team449.robot.jacksonWrappers.MappedAHRS;
import org.usfirst.frc.team449.robot.jacksonWrappers.RotPerSecCANTalon;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.logger.Loggable;
import org.usfirst.frc.team449.robot.other.MotionProfileData;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.TwoSideMPSubsystem.SubsystemMPTwoSides;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;


/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class DriveTalonCluster extends YamlSubsystem implements SubsystemNavX, DriveUnidirectional, Loggable, SubsystemMPTwoSides {

	/**
	 * Joystick scaling constant. Joystick output is scaled by this before being handed to the PID loop to give the loop
	 * space to compensate.
	 */
	protected final double PID_SCALE;

	/**
	 * Right master Talon
	 */
	@NotNull
	protected final RotPerSecCANTalon rightMaster;

	/**
	 * Left master Talon
	 */
	@NotNull
	protected final RotPerSecCANTalon leftMaster;

	/**
	 * The NavX gyro
	 */
	@NotNull
	private final AHRS navX;

	/**
	 * A helper class that loads and runs profiles on the Talons.
	 */
	@NotNull
	private final CANTalonMPComponent mpHandler;

	/**
	 * Whether or not to use the NavX for driving straight
	 */
	private boolean overrideNavX;

	/**
	 * Default constructor.
	 *
	 * @param leftMaster  The master talon on the left side of the drive.
	 * @param rightMaster The master talon on the right side of the drive.
	 * @param navX        The NavX gyro for calculating this drive's heading and angular velocity.
	 * @param MPHandler   The motion profile handler that runs this drive's motion profiles.
	 * @param PIDScale    The amount to scale the output to the PID loop by. Defaults to 1.
	 */
	@JsonCreator
	public DriveTalonCluster(@NotNull @JsonProperty(required = true) RotPerSecCANTalon leftMaster,
	                         @NotNull @JsonProperty(required = true) RotPerSecCANTalon rightMaster,
	                         @NotNull @JsonProperty(required = true) MappedAHRS navX,
	                         @NotNull @JsonProperty(required = true) CANTalonMPComponent MPHandler,
	                         @Nullable Double PIDScale) {
		super();
		//Initialize stuff
		this.PID_SCALE = PIDScale != null ? PIDScale : 1.;
		this.rightMaster = rightMaster;
		this.leftMaster = leftMaster;
		this.mpHandler = MPHandler;
		this.navX = navX;
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
	protected void setVBusThrottle(double left, double right) {
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
	protected void setPIDThrottle(double left, double right) {
		//scale by the max speed
		if (leftMaster.getMaxSpeed() == null || rightMaster.getMaxSpeed() == null) {
			setVBusThrottle(left, right);
			System.out.println("You're trying to set PID throttle, but the drive talons don't have PID constants defined. Using voltage control instead.");
		} else {
			leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeed()));
			rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeed()));
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
	 * Get the velocity of the left side of the drive.
	 *
	 * @return The signed velocity in rotations per second, or null if the drive doesn't have encoders.
	 */
	@Override
	@Nullable
	public Double getLeftVel() {
		return leftMaster.getSpeed();
	}

	/**
	 * Get the velocity of the right side of the drive.
	 *
	 * @return The signed velocity in rotations per second, or null if the drive doesn't have encoders.
	 */
	@Override
	@Nullable
	public Double getRightVel() {
		return rightMaster.getSpeed();
	}

	/**
	 * Get the position of the left side of the drive.
	 *
	 * @return The signed position in inches, or null if the drive doesn't have encoders.
	 */
	@Nullable
	@Override
	public Double getLeftPos() {
		Double feet = leftMaster.nativeToFeet(leftMaster.getCanTalon().getEncPosition());
		if (feet == null) {
			return null;
		} else {
			return feet / 12.;
		}
	}

	/**
	 * Get the position of the right side of the drive.
	 *
	 * @return The signed position in inches, or null if the drive doesn't have encoders.
	 */
	@Nullable
	@Override
	public Double getRightPos() {
		Double feet = rightMaster.nativeToFeet(rightMaster.getCanTalon().getEncPosition());
		if (feet == null) {
			return null;
		} else {
			return feet / 12.;
		}
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
		leftMaster.getCanTalon().enable();
		rightMaster.getCanTalon().enable();
	}

	/**
	 * Stuff run on first enable.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing, the default command gets set with setDefaultCommandManual
	}

	/**
	 * Set the default command. Done here instead of in initDefaultCommand so we don't have a defaultCommand during
	 * auto.
	 *
	 * @param defaultCommand The command to have run by default. Must require this subsystem.
	 */
	public void setDefaultCommandManual(Command defaultCommand) {
		setDefaultCommand(defaultCommand);
	}

	/**
	 * Get the robot's heading using the navX
	 *
	 * @return robot heading, in degrees, on [-180, 180]
	 */
	@Override
	public double getGyroHeading() {
		return navX.pidGet();
	}

	/**
	 * @return true if the NavX is currently overriden, false otherwise.
	 */
	@Override
	public boolean getOverrideNavX() {
		return overrideNavX;
	}

	/**
	 * @param override true to override the NavX, false to un-override it.
	 */
	@Override
	public void setOverrideNavX(boolean override) {
		overrideNavX = override;
	}

	/**
	 * @return An AHRS object representing this subsystem's NavX.
	 */
	@Override
	@NotNull
	public AHRS getNavX() {
		return navX;
	}

	/**
	 * Get the headers for the data this subsystem logs every loop.
	 *
	 * @return An N-length array of String labels for data, where N is the length of the Object[] returned by getData().
	 */
	@Override
	@NotNull
	@Contract(pure = true)
	public String[] getHeader() {
		return new String[]{"left_vel",
				"right_vel",
				"left_setpoint",
				"right_setpoint",
				"left_current",
				"right_current",
				"left_voltage",
				"right_voltage",
				"left_pos",
				"right_pos"};
	}

	/**
	 * Get the data this subsystem logs every loop.
	 *
	 * @return An N-length array of Objects, where N is the number of labels given by getHeader.
	 */
	@Override
	@NotNull
	public Object[] getData() {
		return new Object[]{leftMaster.getSpeed(),
				rightMaster.getSpeed(),
				leftMaster.getSetpoint(),
				rightMaster.getSetpoint(),
				leftMaster.getCanTalon().getOutputCurrent(),
				rightMaster.getCanTalon().getOutputCurrent(),
				leftMaster.getCanTalon().getOutputVoltage(),
				rightMaster.getCanTalon().getOutputVoltage(),
				leftMaster.getCanTalon().getPosition(),
				rightMaster.getCanTalon().getPosition()};
	}

	/**
	 * Get the name of this object.
	 *
	 * @return A string that will identify this object in the log file.
	 */
	@Override
	@NotNull
	@Contract(pure = true)
	public String getName() {
		return "Drive";
	}

	/**
	 * Loads a profile into the MP buffer.
	 *
	 * @param profile The profile to be loaded.
	 */
	@Override
	public void loadMotionProfile(@NotNull MotionProfileData profile) {
		mpHandler.loadTopLevel(profile);
	}

	/**
	 * Start running the profile that's currently loaded into the MP buffer.
	 */
	@Override
	public void startRunningLoadedProfile() {
		mpHandler.startRunningProfile();
	}

	/**
	 * Get whether this subsystem has finished running the profile loaded in it.
	 *
	 * @return true if there's no profile loaded and no profile running, false otherwise.
	 */
	@Override
	public boolean profileFinished() {
		return mpHandler.isFinished();
	}

	/**
	 * Disable the motors.
	 */
	@Override
	public void disable() {
		mpHandler.disableTalons();
	}

	/**
	 * Hold the current position.
	 */
	@Override
	public void holdPosition() {
		mpHandler.holdTalons();
	}

	/**
	 * Get whether the subsystem is ready to run the loaded profile.
	 *
	 * @return true if a profile is loaded and ready to run, false otherwise.
	 */
	@Override
	public boolean readyToRunProfile() {
		return mpHandler.isReady();
	}

	/**
	 * Stops any MP-related threads currently running. Normally called at the start of teleop.
	 */
	@Override
	public void stopMPProcesses() {
		mpHandler.stopUpdaterProcess();
	}

	/**
	 * Loads given profiles into the left and right sides of the drive.
	 *
	 * @param left  The profile to load into the left side.
	 * @param right The profile to load into the right side.
	 */
	@Override
	public void loadMotionProfile(@NotNull MotionProfileData left, @NotNull MotionProfileData right) {
		mpHandler.loadIndividualProfiles(new MotionProfileData[]{left, right});
	}

	public void resetPosition() {
		leftMaster.getCanTalon().setEncPosition(0);
		rightMaster.getCanTalon().setEncPosition(0);
	}
}
