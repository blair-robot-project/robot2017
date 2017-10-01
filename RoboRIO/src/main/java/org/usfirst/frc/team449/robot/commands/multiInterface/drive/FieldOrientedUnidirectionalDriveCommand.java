package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.oi.fieldoriented.OIFieldOriented;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.commands.PIDAngleCommand;

/**
 * Unidirectional drive with field-oriented control
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FieldOrientedUnidirectionalDriveCommand<T extends YamlSubsystem & DriveUnidirectional & SubsystemNavX> extends PIDAngleCommand {

	/**
	 * The drive this command is controlling.
	 */
	@NotNull
	protected final T subsystem;

	/**
	 * The OI giving the input stick values.
	 */
	@NotNull
	protected final OIFieldOriented oi;

	/**
	 * Default constructor
	 *
	 * @param toleranceBuffer             How many consecutive loops have to be run while within tolerance to be
	 *                                    considered on target. Multiply by loop period of ~20 milliseconds for time.
	 *                                    Defaults to 0.
	 * @param absoluteTolerance           The maximum number of degrees off from the target at which we can be
	 *                                    considered within tolerance.
	 * @param minimumOutput               The minimum output of the loop. Defaults to zero.
	 * @param maximumOutput               The maximum output of the loop. Can be null, and if it is, no maximum output
	 *                                    is used.
	 * @param deadband                    The deadband around the setpoint, in degrees, within which no output is given
	 *                                    to the motors. Defaults to zero.
	 * @param maxAngularVelToEnterLoop    The maximum angular velocity, in degrees/sec, at which the loop will be
	 *                                    entered. Defaults to 180.
	 * @param inverted                    Whether the loop is inverted. Defaults to false.
	 * @param kP                          Proportional gain. Defaults to zero.
	 * @param kI                          Integral gain. Defaults to zero.
	 * @param kD                          Derivative gain. Defaults to zero.
	 * @param subsystem                   The drive to execute this command on.
	 * @param oi                          The OI controlling the robot.
	 */
	@JsonCreator
	public FieldOrientedUnidirectionalDriveCommand(@JsonProperty(required = true) double absoluteTolerance,
	                                               int toleranceBuffer,
	                                               double minimumOutput, @Nullable Double maximumOutput,
	                                               double deadband,
	                                               @Nullable Double maxAngularVelToEnterLoop,
	                                               boolean inverted,
	                                               int kP,
	                                               int kI,
	                                               int kD,
	                                               @NotNull @JsonProperty(required = true) T subsystem,
	                                               @NotNull @JsonProperty(required = true) OIFieldOriented oi) {
		//Assign stuff
		super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, subsystem, kP, kI, kD);
		this.oi = oi;
		this.subsystem = subsystem;

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
		Logger.addEvent("FieldOrientedUnidirectionalDriveCommand init.", this.getClass());
	}

	/**
	 * Decide whether or not we should be in free drive or straight drive, and log data.
	 */
	@Override
	protected void execute() {
		//Do nothing
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
		Logger.addEvent("FieldOrientedUnidirectionalDriveCommand End.", this.getClass());
	}

	/**
	 * Stop the motors and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("FieldOrientedUnidirectionalDriveCommand Interrupted! Stopping the robot.", this.getClass());
		subsystem.fullStop();
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

			//Adjust the heading according to the PID output, it'll be positive if we want to go right.
			subsystem.setOutput(oi.getLeftOutput() - output, oi.getRightOutput() + output);
		}
		//If we're free driving...
		else {
			//Set the throttle to normal arcade throttle.
			subsystem.setOutput(oi.getLeftOutput(), oi.getRightOutput());
		}
	}
}