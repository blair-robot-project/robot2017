package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.fasterxml.jackson.annotation.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands.PIDAngleCommand;
import org.usfirst.frc.team449.robot.util.BufferTimer;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;

/**
 * Drive with arcade drive setup, and when the driver isn't turning, use a NavX to stabilize the robot's alignment.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class UnidirectionalNavXArcadeDrive<T extends YamlSubsystem & UnidirectionalDrive & NavxSubsystem> extends PIDAngleCommand {
	/**
	 * The UnidirectionalDrive this command is controlling.
	 */
	@NotNull
	protected final T subsystem;

	/**
	 * The OI giving the vel and turn stick values.
	 */
	@NotNull
	protected final ArcadeOI oi;

	/**
	 * The maximum velocity for the robot to be at in order to switch to driveStraight, in degrees/sec
	 */
	private final double maxAngularVelToEnterLoop;

	/**
	 * A bufferTimer so we only switch to driving straight when the conditions are met for a certain period of time.
	 */
	@NotNull
	private final BufferTimer driveStraightLoopEntryTimer;

	/**
	 * Whether or not we should be using the NavX to drive straight stably.
	 */
	private boolean drivingStraight;

	/**
	 * The velocity input from OI. Should be between -1 and 1.
	 */
	private double vel;

	/**
	 * The rotation input from OI. Should be between -1 and 1.
	 */
	private double rot;

	/**
	 * Default constructor
	 *
	 * @param toleranceBuffer          How many consecutive loops have to be run while within tolerance to be
	 *                                 considered
	 *                                 on target. Multiply by loop period of ~20 milliseconds for time. Defaults to 0.
	 * @param absoluteTolerance        The maximum number of degrees off from the target at which we can be considered
	 *                                 within tolerance.
	 * @param minimumOutput            The minimum output of the loop. Defaults to zero.
	 * @param maximumOutput            The maximum output of the loop. Can be null, and if it is, no maximum output is
	 *                                 used.
	 * @param deadband                 The deadband around the setpoint, in degrees, within which no output is given to
	 *                                 the motors. Defaults to zero.
	 * @param maxAngularVelToEnterLoop The maximum angular velocity, in degrees/sec, at which the loop will be entered.
	 *                                 Defaults to 180.
	 * @param inverted                 Whether the loop is inverted. Defaults to false.
	 * @param kP                       Proportional gain. Defaults to zero.
	 * @param kI                       Integral gain. Defaults to zero.
	 * @param kD                       Derivative gain. Defaults to zero.
	 * @param loopEntryDelay           The delay to enter the loop after conditions for entry are met. Defaults to
	 *                                 zero.
	 * @param subsystem                The drive to execute this command on.
	 * @param oi                       The OI controlling the robot.
	 */
	@JsonCreator
	public UnidirectionalNavXArcadeDrive(@JsonProperty(required = true) double absoluteTolerance,
	                                     int toleranceBuffer,
	                                     double minimumOutput, @Nullable Double maximumOutput,
	                                     double deadband,
	                                     @Nullable Double maxAngularVelToEnterLoop,
	                                     boolean inverted,
	                                     int kP,
	                                     int kI,
	                                     int kD,
	                                     double loopEntryDelay,
	                                     @NotNull @JsonProperty(required = true) T subsystem,
	                                     @NotNull @JsonProperty(required = true) ArcadeOI oi) {
		//Assign stuff
		super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, subsystem, kP, kI, kD);
		this.oi = oi;
		this.subsystem = subsystem;

		driveStraightLoopEntryTimer = new BufferTimer(loopEntryDelay);
		if (maxAngularVelToEnterLoop == null) {
			maxAngularVelToEnterLoop = 180.;
		}
		this.maxAngularVelToEnterLoop = maxAngularVelToEnterLoop;

		//Needs a requires because it's a default command.
		requires(this.subsystem);

		//Logging, but in Spanish.
		Logger.addEvent("Drive Robot bueno", this.getClass());
	}

	/**
	 * Initialize PIDController and variables.
	 */
	@Override
	protected void initialize() {
		//Reset all values of the PIDController and enable it.
		this.getPIDController().reset();
		this.getPIDController().enable();
		Logger.addEvent("UnidirectionalNavXArcadeDrive init.", this.getClass());

		//Initial assignment
		drivingStraight = false;
		vel = oi.getFwd();
		rot = oi.getRot();
	}

	/**
	 * Decide whether or not we should be in free drive or straight drive, and log data.
	 */
	@Override
	protected void execute() {
		//Set vel and rot to what they should be.
		vel = oi.getFwd();
		rot = oi.getRot();

		//If we're driving straight but the driver tries to turn or overrides the NavX:
		if (drivingStraight && (rot != 0 || subsystem.getOverrideNavX())) {
			//Switch to free drive
			drivingStraight = false;
			Logger.addEvent("Switching to free drive.", this.getClass());
		}
		//If we're free driving and the driver lets go of the turn stick:
		else if (driveStraightLoopEntryTimer.get(!(subsystem.getOverrideNavX()) && !(drivingStraight) &&
				rot == 0 && Math.abs(subsystem.getNavX().getRate()) <= maxAngularVelToEnterLoop)) {
			//Switch to driving straight
			drivingStraight = true;
			//Set the setpoint to the current heading and reset the NavX
			this.getPIDController().reset();
			this.getPIDController().setSetpoint(subsystem.getGyroOutput());
			this.getPIDController().enable();
			Logger.addEvent("Switching to DriveStraight.", this.getClass());
		}

		//Log data and stuff
		SmartDashboard.putBoolean("driving straight?", drivingStraight);
		SmartDashboard.putBoolean("Override Navx", subsystem.getOverrideNavX());
		SmartDashboard.putNumber("Vel Axis", vel);
		SmartDashboard.putNumber("Rot axis", rot);
	}

	/**
	 * Run constantly because this is a defaultDrive
	 *
	 * @return false
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("UnidirectionalNavXArcadeDrive End.", this.getClass());
	}

	/**
	 * Stop the motors and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("UnidirectionalNavXArcadeDrive Interrupted! Stopping the robot.", this.getClass());
		subsystem.setOutput(0.0, 0.0);
	}

	/**
	 * Give the correct output to the motors based on whether we're in free drive or drive straight.
	 *
	 * @param output The output of the angular PID loop.
	 */
	@Override
	protected void usePIDOutput(double output) {
		//If we're driving straight..
		if (drivingStraight) {
			//Process the output (minimumOutput, deadband, etc.)
			output = processPIDOutput(output);

			//Log stuff
			SmartDashboard.putNumber("PID output", output);

			//Adjust the heading according to the PID output, it'll be positive if we want to go right.
			subsystem.setOutput(vel - output, vel + output);
		}
		//If we're free driving...
		else {
			//Set the throttle to normal arcade throttle.
			subsystem.setOutput(oi.getLeftOutput(), oi.getRightOutput());
		}
	}
}