package org.usfirst.frc.team449.robot.commands.multiInterface.drive;

import com.fasterxml.jackson.annotation.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.components.AutoshiftComponent;
import org.usfirst.frc.team449.robot.drive.shifting.DriveShiftable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveUnidirectional;
import org.usfirst.frc.team449.robot.jacksonWrappers.YamlSubsystem;
import org.usfirst.frc.team449.robot.logger.Logger;
import org.usfirst.frc.team449.robot.oi.unidirectional.OIUnidirectional;
import org.usfirst.frc.team449.robot.other.BufferTimer;
import org.usfirst.frc.team449.robot.subsystem.interfaces.navX.SubsystemNavX;

/**
 * Drive with arcade drive setup, autoshift, and when the driver isn't turning, use a NavX to stabilize the robot's
 * alignment.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class UnidirectionalNavXShiftingDefaultDrive <T extends YamlSubsystem & DriveUnidirectional & SubsystemNavX & DriveShiftable> extends UnidirectionalNavXDefaultDrive {

	/**
	 * The drive to execute this command on.
	 */
	@NotNull
	protected final T subsystem;

	/**
	 * The helper object for autoshifting.
	 */
	@NotNull
	protected final AutoshiftComponent autoshiftComponent;

	/**
	 * Default constructor
	 *
	 * @param toleranceBuffer             How many consecutive loops have to be run while within tolerance to be
	 *                                    considered on target. Multiply by loop period of ~20 milliseconds for time.
	 *                                    Defaults to  0.
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
	 * @param driveStraightLoopEntryTimer The buffer timer for starting to drive straight.
	 * @param subsystem                   The drive to execute this command on.
	 * @param oi                          The OI controlling the robot.
	 * @param autoshiftComponent          The helper object for autoshifting.
	 */
	@JsonCreator
	public UnidirectionalNavXShiftingDefaultDrive(@JsonProperty(required = true) double absoluteTolerance,
	                                              int toleranceBuffer,
	                                              double minimumOutput, @Nullable Double maximumOutput,
	                                              double deadband,
	                                              @Nullable Double maxAngularVelToEnterLoop,
	                                              boolean inverted,
	                                              int kP,
	                                              int kI,
	                                              int kD,
	                                              @NotNull @JsonProperty(required = true) BufferTimer driveStraightLoopEntryTimer,
	                                              @NotNull @JsonProperty(required = true) T subsystem,
	                                              @NotNull @JsonProperty(required = true) OIUnidirectional oi,
	                                              @NotNull @JsonProperty(required = true) AutoshiftComponent autoshiftComponent) {
		super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, maxAngularVelToEnterLoop,
				inverted, kP, kI, kD, driveStraightLoopEntryTimer, subsystem, oi);
		this.autoshiftComponent = autoshiftComponent;
		this.subsystem = subsystem;
	}

	/**
	 * Autoshift, decide whether or not we should be in free drive or straight drive, and log data.
	 */
	@Override
	public void execute() {
		//Auto-shifting
		autoshiftComponent.autoshift(oi.getLeftOutput(), oi.getRightOutput(), subsystem.getLeftVel(),
				subsystem.getRightVel(), gear -> subsystem.setGear(gear));
		super.execute();
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("ShiftingUnidirectionalNavXArcadeDrive End.", this.getClass());
	}

	/**
	 * Stop the motors and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("ShiftingUnidirectionalNavXArcadeDrive Interrupted! Stopping the robot.", this.getClass());
		subsystem.fullStop();
	}
}
