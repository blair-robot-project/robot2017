package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import maps.org.usfirst.frc.team449.robot.util.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Turn a certain number of degrees from the current heading.
 */
public class NavXRelativeTTA extends NavXTurnToAngle {

	/**
	 * Default constructor.
	 *
	 * @param map      An turnPID map with PID values, an absolute tolerance, and minimum output.
	 * @param setpoint The setpoint, in degrees from 180 to -180.
	 * @param drive    The drive subsystem to execute this command on.
	 * @param timeout  How long this command is allowed to run for, in seconds. Needed because sometimes floating-point
	 *                 errors prevent termination.
	 */
	public NavXRelativeTTA(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, double setpoint, UnidirectionalDrive drive,
	                       double timeout) {
		super(map, setpoint, drive, timeout);
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
