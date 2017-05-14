package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands.PIDAngleCommand;

/**
 * Turns to a specified angle, relative to the angle the NavX was at when the robot was turned on.
 */
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
	 * @param map      An turnPID map with PID values, an absolute tolerance, and minimum output.
	 * @param setpoint The setpoint, in degrees from 180 to -180.
	 * @param drive    The drive subsystem to execute this command on. Must also be a NavX subsystem.
	 * @param timeout  How long this command is allowed to run for, in seconds. Needed because sometimes floating-point errors prevent termination.
	 */
	public NavXTurnToAngle(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, double setpoint, UnidirectionalDrive drive,
	                       double timeout) {
		super(map, (NavxSubsystem) drive);
		this.drive = drive;
		this.setpoint = setpoint;
		//Convert from seconds to milliseconds
		this.timeout = (long) (timeout * 1000);
		requires((Subsystem) drive);
	}

	/**
	 * Clip a degree number to the NavX's -180 to 180 system.
	 *
	 * @param theta The angle to clip, in degrees.
	 * @return The equivalent of that number, clipped to be between -180 and 180.
	 */
	public static double clipTo180(double theta) {
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
	 * Log data to a file and SmartDashboard.
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
		//The PIDController onTarget() is crap and sometimes never terminates because of floating point errors, so we have a timeout
		return this.getPIDController().onTarget() || Robot.currentTimeMillis() - startTime > timeout;
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
