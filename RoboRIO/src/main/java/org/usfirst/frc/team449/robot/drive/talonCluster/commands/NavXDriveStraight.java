package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.components.PID;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.TankOI;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands.PIDAngleCommand;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Drives straight using the NavX gyro to keep a constant alignment.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class NavXDriveStraight extends PIDAngleCommand {

	/**
	 * The tank OI to get input from.
	 */
	private TankOI oi;

	/**
	 * The drive subsystem to give output to.
	 */
	private UnidirectionalDrive drive;

	/**
	 * Whether to use the left joystick to drive straight.
	 */
	private boolean useLeft;

	/**
	 * Default constructor.
	 *
	 * @param PID                      The PID gains for this loop.
	 * @param toleranceBuffer          How many consecutive loops have to be run while within tolerance to be considered
	 *                                 on target. Multiply by loop period of ~20 milliseconds for time. Defaults to 0.
	 * @param absoluteTolerance        The maximum number of degrees off from the target at which we can be considered
	 *                                 within tolerance.
	 * @param minimumOutput            The minimum output of the loop. Defaults to zero.
	 * @param maximumOutput            The maximum output of the loop. Can be null, and if it is, no maximum output is
	 *                                 used.
	 * @param deadband                 The deadband around the setpoint, in degrees, within which no output is given to
	 *                                 the motors. Defaults to zero.
	 *                                 Defaults to 180.
	 * @param inverted                 Whether the loop is inverted. Defaults to false.
	 * @param drive   The drive to execute this command on.
	 * @param oi      The tank OI to take input from.
	 * @param useLeft Which joystick to use to get the forward component to drive straight. True for left, false for
	 *                right.
	 */
	@JsonCreator
	public <T extends YamlSubsystem & UnidirectionalDrive & NavxSubsystem> NavXDriveStraight(@JsonProperty(required = true) PID PID,
	                                                                                     @JsonProperty(required = true) double absoluteTolerance,
	                                                                                     int toleranceBuffer,
	                                                                                     double minimumOutput, Double maximumOutput,
	                                                                                     double deadband,
	                                                                                     boolean inverted,
	                                                                                     @JsonProperty(required = true) T drive,
	                                                                                     @JsonProperty(required = true) TankOI oi,
	                                                                                     @JsonProperty(required = true) boolean useLeft) {
		super(PID, absoluteTolerance, toleranceBuffer, minimumOutput, maximumOutput, deadband, inverted, drive);
		this.oi = oi;
		this.drive = drive;
		this.useLeft = useLeft;
		//This is likely to need to interrupt the DefaultCommand and therefore should require its subsystem.
		requires(drive);
	}

	/**
	 * Give output to the drive based on the out of the PID loop.
	 *
	 * @param output the value the PID loop calculated
	 */
	@Override
	protected void usePIDOutput(double output) {
		//Process the PID output with deadband, minimum output, etc.
		output = processPIDOutput(output);

		//Log processed output.
		SmartDashboard.putNumber("NavXDriveStraight PID output", output);

		//Set throttle to the specified stick.
		double throttle;
		if (useLeft) {
			throttle = oi.getLeftThrottle();
		} else {
			throttle = oi.getRightThrottle();
		}

		//Set the output to the throttle velocity adjusted by the PID output.
		drive.setOutput(throttle - output, throttle + output);
	}

	/**
	 * Set the setpoint of the angle PID.
	 */
	@Override
	protected void initialize() {
		this.getPIDController().setSetpoint(this.returnPIDInput());
		this.getPIDController().enable();
	}

	/**
	 * Does nothing, the actual work is done in usePIDOutput.
	 */
	@Override
	protected void execute() {
		//nada.
	}

	/**
	 * Finishes instantaneously.
	 *
	 * @return true
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
		Logger.addEvent("NavXDriveStraight end", this.getClass());
		this.getPIDController().disable();
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("NavXDriveStraight interrupted!", this.getClass());
		this.getPIDController().disable();
	}
}
