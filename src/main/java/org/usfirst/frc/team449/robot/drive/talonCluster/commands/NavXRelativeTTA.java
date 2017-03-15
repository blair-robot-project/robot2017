package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Turn a ceetain number of degrees from the current heading.
 */
public class NavXRelativeTTA extends NavXTurnToAngle {

	/**
	 * Default constructor.
	 *
	 * @param map   An turnPID map with PID values, an absolute tolerance, and minimum output.
	 * @param setpoint    The setpoint, in degrees from 180 to -180.
	 * @param drive The drive subsystem to execute this command on.
	 * @param timeout How long this command is allowed to run for, in seconds. Needed because sometimes floating-point errors prevent termination.
	 */
	public NavXRelativeTTA(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, double setpoint, TalonClusterDrive drive,
	                       double timeout) {
		super(map, setpoint, drive, timeout);
	}

	/**
	 * Set up the start time and setpoint.
	 */
	@Override
	protected void initialize() {
		//Setup start time
		this.startTime = System.currentTimeMillis();
		System.out.println("NavXRelativeTurnToAngle init.");
		//Do math to setup the setpoint.
		this.setSetpoint(clipTo180(drive.getGyroOutput() + setpoint));
		//Make sure to enable the controller!
		this.getPIDController().enable();
	}

	/**
	 * Log when the command ends.
	 */
	@Override
	protected void end() {
		System.out.println("NavXRelativeTurnToAngle end.");
		//Stop the controller
		this.getPIDController().disable();
	}

	/**
	 * Log when the command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("NavXRelativeTurnToAngle interrupted!");
		//Stop the controller
		this.getPIDController().disable();
	}
}
