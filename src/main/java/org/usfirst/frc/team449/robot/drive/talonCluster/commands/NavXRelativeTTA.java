package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Program created by noah on 1/23/17.
 */
public class NavXRelativeTTA extends NavXTurnToAngle {

	/**
	 * Default constructor.
	 *
	 * @param map   An turnPID map with PID values, an absolute tolerance, and minimum output.
	 * @param sp    The setpoint, in degrees from 180 to -180.
	 * @param drive The drive subsystem whose motors this is controlling.
	 */
	public NavXRelativeTTA(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, double sp, TalonClusterDrive drive, double timeout) {
		super(map, sp, drive, timeout);
	}

	@Override
	protected void initialize() {
		this.startTime = System.currentTimeMillis();
		System.out.println("NavXRelativeTurnToAngle init.");
		this.setSetpoint(clipTo180(drive.getGyroOutput() + sp));
		//Make sure to enable the controller!
		this.getPIDController().enable();
	}


	@Override
	protected void end() {
		System.out.println("NavXRelativeTurnToAngle end.");
		this.getPIDController().disable();
	}

	@Override
	protected void interrupted() {
		System.out.println("NavXRelativeTurnToAngle interrupted!");
		this.getPIDController().disable();
	}
}
