package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Turn a certain number of degrees from the current heading.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class NavXRelativeTTA <T extends YamlSubsystem & UnidirectionalDrive & NavxSubsystem> extends NavXTurnToAngle {

	/**
	 * Default constructor.
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
	 * @param inverted                 Whether the loop is inverted. Defaults to false.
	 * @param kP Proportional gain. Defaults to zero.
	 * @param kI Integral gain. Defaults to zero.
	 * @param kD Derivative gain. Defaults to zero.
	 * @param setpoint The setpoint, in degrees from 180 to -180.
	 * @param drive    The drive subsystem to execute this command on.
	 * @param timeout  How long this command is allowed to run for, in seconds. Needed because sometimes floating-point
	 *                 errors prevent termination.
	 */
	@JsonCreator
	public NavXRelativeTTA(@JsonProperty(required = true) double absoluteTolerance,
	                                                                                       int toleranceBuffer,
	                                                                                       double minimumOutput, Double maximumOutput,
	                                                                                       double deadband,
	                                                                                       boolean inverted,
	                                                                                       int kP,
	                                                                                       int kI,
	                                                                                       int kD,
	                                                                                       double setpoint,
	                                                                                       T drive,
	                                                                                       double timeout) {
		super(absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, kP, kI, kD, setpoint, drive, timeout);
	}

	/**
	 * Set up the start time and setpoint.
	 */
	@Override
	protected void initialize() {
		//Setup start time
		this.startTime = Robot.currentTimeMillis();
		Logger.addEvent("NavXRelativeTurnToAngle init.", this.getClass());
		//Do math to setup the setpoint.
		this.setSetpoint(clipTo180(((NavxSubsystem) drive).getGyroOutput() + setpoint));
		//Make sure to enable the controller!
		this.getPIDController().enable();
	}

	/**
	 * Log when the command ends.
	 */
	@Override
	protected void end() {
		Logger.addEvent("NavXRelativeTurnToAngle end.", this.getClass());
		//Stop the controller
		this.getPIDController().disable();
	}

	/**
	 * Log when the command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("NavXRelativeTurnToAngle interrupted!", this.getClass());
		//Stop the controller
		this.getPIDController().disable();
	}
}
