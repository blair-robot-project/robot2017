package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Turns to a specified angle, relative to the angle the NavX was at when the robot was turned on.
 */
public class NavXTurnToAngle extends PIDAngleCommand {

	/**
	 * The drive subsystem to execute this command on and to get the gyro reading from.
	 */
	protected TalonClusterDrive drive;

	/**
	 * The angle to turn to.
	 */
	protected double setpoint;

	/**
	 * How long this command is allowed to run for (in milliseconds)
	 */
	protected long timeout;

	/**
	 * The time this command was initiated
	 */
	protected long startTime;

	/**
	 * Default constructor.
	 *
	 * @param map   An turnPID map with PID values, an absolute tolerance, and minimum output.
	 * @param setpoint    The setpoint, in degrees from 180 to -180.
	 * @param drive The drive subsystem to execute this command on.
	 * @param timeout How long this command is allowed to run for, in seconds. Needed because sometimes floating-point errors prevent termination.
	 */
	public NavXTurnToAngle(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, double setpoint, TalonClusterDrive drive,
	                       double timeout) {
		super(map, drive);
		this.drive = drive;
		this.setpoint = setpoint;
		//Convert from seconds to milliseconds
		this.timeout = (long) (timeout * 1000);
		requires(drive);
	}

	/**
	 * Clip a degree number to the NavX's -180 to 180 system.
	 * @param theta The angle to clip, in degrees.
	 * @return The equivalent of that number, clipped to be between -180 and 180.
	 */
	public static double clipTo180(double theta) {
		return (theta + 180) % 360 - 180;
	}

	/**
	 * Give output to the motors based on the output of the PID loop
	 * @param output The output of the angle PID loop
	 */
	@Override
	protected void usePIDOutput(double output) {
		//Logging
		SmartDashboard.putNumber("Preprocessed output", output);
		SmartDashboard.putNumber("NavX Turn To Angle Setpoint", this.getSetpoint());
		if (minimumOutputEnabled) {
			//Set the output to the minimum if it's too small.
			if (output > 0 && output < minimumOutput)
				output = minimumOutput;
			else if (output < 0 && output > -minimumOutput)
				output = -minimumOutput;
		}
		//If we're within deadband degrees of the setpoint, we stop moving to avoid "dancing" around the setpoint.
		if (Math.abs(this.getPIDController().getError()) < deadband) {
			output = 0;
		}
		//More logging
		SmartDashboard.putNumber("Processed output", output);

		//Which one of these is negative may be different from robot to robot, we don't know.
		drive.setDefaultThrottle(output, -output);    //spin to the right angle
	}

	/**
	 * Set up the start time and setpoint.
	 */
	@Override
	protected void initialize() {
		//Set up start time
		this.startTime = System.currentTimeMillis();
		this.setSetpoint(setpoint);
		//Make sure to enable the controller!
		this.getPIDController().enable();
	}

	/**
	 * Log data to a file and SmartDashboard.
	 */
	@Override
	protected void execute() {
		drive.logData();
		SmartDashboard.putBoolean("onTarget", this.getPIDController().onTarget());
		SmartDashboard.putNumber("Avg Navx Error", this.getPIDController().getAvgError());
	}

	/**
	 * Exit when the robot reaches the setpoint or enough time has passed.
	 * @return True if timeout seconds have passed or the robot is on target, false otherwise.
	 */
	@Override
	protected boolean isFinished() {
		//The NavX onTarget() is crap and sometimes never terminates because of floating point errors, so we have a timeout
		return this.getPIDController().onTarget() || System.currentTimeMillis() - startTime > timeout;
	}

	/**
	 * Log when the command ends.
	 */
	@Override
	protected void end() {
		System.out.println("NavXTurnToAngle end.");
		this.getPIDController().disable();
	}

	/**
	 * Log when the command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("NavXTurnToAngle interrupted!");
		this.getPIDController().disable();
	}
}
