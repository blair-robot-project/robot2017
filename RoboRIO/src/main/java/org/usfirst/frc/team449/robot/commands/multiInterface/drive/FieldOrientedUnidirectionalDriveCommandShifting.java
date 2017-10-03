package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.oi.fieldoriented.OIFieldOriented;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.commands.PIDAngleCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Unidirectional drive with field-oriented control
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FieldOrientedUnidirectionalDriveCommandShifting<T extends YamlSubsystem & DriveUnidirectional & SubsystemNavX> extends FieldOrientedUnidirectionalDriveCommand {

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
	 * @param inverted                    Whether the loop is inverted. Defaults to false.
	 * @param kP                          Proportional gain. Defaults to zero.
	 * @param kI                          Integral gain. Defaults to zero.
	 * @param kD                          Derivative gain. Defaults to zero.
	 * @param subsystem                   The drive to execute this command on.
	 * @param oi                          The OI controlling the robot.
	 * @param snapPoints The points to snap the PID controller input to.
	 */
	@JsonCreator
	public FieldOrientedUnidirectionalDriveCommandShifting(@JsonProperty(required = true) double absoluteTolerance,
	                                                       int toleranceBuffer,
	                                                       double minimumOutput, @Nullable Double maximumOutput,
	                                                       double deadband,
	                                                       boolean inverted,
	                                                       int kP,
	                                                       int kI,
	                                                       int kD,
	                                                       @NotNull @JsonProperty(required = true) T subsystem,
	                                                       @NotNull @JsonProperty(required = true) OIFieldOriented oi,
	                                                       @Nullable List<AngularSnapPoint> snapPoints) {
		//Assign stuff
		super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, kP, kI, kD, subsystem, oi, snapPoints);
	}

	/**
	 * Decide whether or not we should be in free drive or straight drive, and log data.
	 */
	@Override
	protected void execute() {
		//Do nothing
		super.execute();
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
		Logger.addEvent("FieldOrientedUnidirectionalDriveCommandShifting End.", this.getClass());
	}

	/**
	 * Stop the motors and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("FieldOrientedUnidirectionalDriveCommandShifting Interrupted! Stopping the robot.", this.getClass());
	}

}