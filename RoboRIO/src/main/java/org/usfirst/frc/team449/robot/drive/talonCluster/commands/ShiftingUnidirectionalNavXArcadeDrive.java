package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.fasterxml.jackson.annotation.*;
import org.usfirst.frc.team449.robot.util.AutoshiftProcessor;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.ArcadeOI;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;

import java.util.function.Consumer;

/**
 * Drive with arcade drive setup, autoshift, and when the driver isn't turning, use a NavX to stabilize the robot's
 * alignment.
 */
@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.WRAPPER_OBJECT, property="@class")
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class ShiftingUnidirectionalNavXArcadeDrive <T extends YamlSubsystem & UnidirectionalDrive & NavxSubsystem & ShiftingDrive> extends UnidirectionalNavXArcadeDrive {

	protected T driveSubsystem;

	protected AutoshiftProcessor autoshiftProcessor;

	/**
	 * Default constructor
	 *
	 * @param toleranceBuffer          How many consecutive loops have to be run while within tolerance to be considered
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
	 * @param kP Proportional gain. Defaults to zero.
	 * @param kI Integral gain. Defaults to zero.
	 * @param kD Derivative gain. Defaults to zero.
	 * @param loopEntryDelay           The delay to enter the loop after conditions for entry are met. Defaults to
	 *                                 zero.
	 * @param drive The drive to execute this command on.
	 * @param oi    The OI controlling the robot.
	 */
	@JsonCreator
	public ShiftingUnidirectionalNavXArcadeDrive(@JsonProperty(required = true) double absoluteTolerance,
	                                             int toleranceBuffer,
	                                             double minimumOutput, Double maximumOutput,
	                                             double deadband,
	                                             Double maxAngularVelToEnterLoop,
	                                             boolean inverted,
	                                             int kP,
	                                             int kI,
	                                             int kD,
	                                             double loopEntryDelay,
	                                             @JsonProperty(required = true) T drive,
	                                             @JsonProperty(required = true) ArcadeOI oi,
	                                             @JsonProperty(required = true) AutoshiftProcessor autoshiftProcessor) {
		super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, maxAngularVelToEnterLoop, inverted, kP, kI, kD, loopEntryDelay, drive, oi);
		this.autoshiftProcessor = autoshiftProcessor;
		this.driveSubsystem = drive;
	}

	/**
	 * Autoshift, decide whether or not we should be in free drive or straight drive, and log data.
	 */
	@Override
	public void execute() {
		//Auto-shifting
		autoshiftProcessor.arcadeAutoshift(oi.getRot(), oi.getFwd(), driveSubsystem.getLeftVel(),
				driveSubsystem.getRightVel(), gear -> driveSubsystem.setGear(gear));

		super.execute();
	}
}
