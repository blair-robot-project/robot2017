package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePID;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands.PIDAngleCommand;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Turns to a specified angle, relative to the angle the NavX was at when the robot was turned on.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class NavXTurnToAngle extends PIDAngleCommand {

	/**
	 * The drive subsystem to execute this command on and to get the gyro reading from.
	 */
	protected UnidirectionalDrive drive;

	/**
	 * The angle to turn to.
	 */
	protected double setpoint;

	/**
	 * The time this command was initiated
	 */
	protected long startTime;

	/**
	 * How long this command is allowed to run for (in milliseconds)
	 */
	private long timeout;

	/**
	 * Default constructor.
	 *
	 * @param PID      The PID values, an absolute tolerance, and minimum output.
	 * @param setpoint The setpoint, in degrees from 180 to -180.
	 * @param drive    The drive subsystem to execute this command on.
	 * @param timeout  How long this command is allowed to run for, in seconds. Needed because sometimes floating-point
	 *                 errors prevent termination.
	 */
	public <T extends Subsystem & UnidirectionalDrive & NavxSubsystem> NavXTurnToAngle(@JsonProperty(required = true) ToleranceBufferAnglePID PID,
	                                                                                   @JsonProperty(required = true) double setpoint,
	                                                                                   @JsonProperty(required = true) T drive,
	                                                                                   @JsonProperty(required = true) double timeout) {
		super(PID, drive);
		this.drive = drive;
		this.setpoint = setpoint;
		//Convert from seconds to milliseconds
		this.timeout = (long) (timeout * 1000);
		requires(drive);
	}

	/**
	 * Clip a degree number to the NavX's -180 to 180 system.
	 *
	 * @param theta The angle to clip, in degrees.
	 * @return The equivalent of that number, clipped to be between -180 and 180.
	 */
	protected static double clipTo180(double theta) {
		return (theta + 180) % 360 - 180;
	}

	/**
	 * Give output to the motors based on the output of the PID loop
	 *
	 * @param output The output of the angle PID loop
	 */
	@Override
	protected void usePIDOutput(double output) {
		//Logging
		SmartDashboard.putNumber("Preprocessed output", output);
		SmartDashboard.putNumber("NavX Turn To Angle Setpoint", getSetpoint());

		//Process the output with deadband, minimum output, etc.
		output = processPIDOutput(output);

		//More logging
		SmartDashboard.putNumber("NavXTurnToAngle PID loop output", output);

		drive.setOutput(-output, output);    //spin to the right angle
	}

	/**
	 * Set up the start time and setpoint.
	 */
	@Override
	protected void initialize() {
		//Set up start time
		this.startTime = Robot.currentTimeMillis();
		this.setSetpoint(setpoint);
		//Make sure to enable the controller!
		this.getPIDController().enable();
	}

	/**
	 * Log data to SmartDashboard.
	 */
	@Override
	protected void execute() {
		SmartDashboard.putBoolean("onTarget", this.getPIDController().onTarget());
		SmartDashboard.putNumber("Avg Navx Error", this.getPIDController().getAvgError());
	}

	/**
	 * Exit when the robot reaches the setpoint or enough time has passed.
	 *
	 * @return True if timeout seconds have passed or the robot is on target, false otherwise.
	 */
	@Override
	protected boolean isFinished() {
		//The PIDController onTarget() is crap and sometimes never terminates because of floating point errors, so we need a timeout
		return this.getPIDController().onTarget() || Robot.currentTimeMillis() - startTime > timeout;
	}

	/**
	 * Log when the command ends.
	 */
	@Override
	protected void end() {
		Logger.addEvent("NavXTurnToAngle end.", this.getClass());
		this.getPIDController().disable();
	}

	/**
	 * Log when the command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("NavXTurnToAngle interrupted!", this.getClass());
		this.getPIDController().disable();
	}
}
