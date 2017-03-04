package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Drives straight using the NavX gyro to keep a constant alignment.
 */
//TODO update this to the new OI organization.
public class NavXDriveStraight extends PIDAngleCommand {

	private OISubsystem oi;
	private TalonClusterDrive drive;
	private double sp;

	public NavXDriveStraight(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, TalonClusterDrive drive,
	                         OISubsystem oi) {
		super(map, drive);
		this.oi = oi;
		this.drive = drive;
		requires(drive);
	}

	/**
	 * Give output to the drive based on the out of the PID loop.
	 *
	 * @param output the value the PID loop calculated
	 */
	@Override
	protected void usePIDOutput(double output) {
		if (minimumOutputEnabled && this.getPIDController().getError() * 3 / 4 > tolerance) {
			//Set the output to the minimum if it's too small.
			if (output > 0 && output < minimumOutput)
				output = minimumOutput;
			else if (output < 0 && output > -minimumOutput)
				output = -minimumOutput;
		}
		if (this.getPIDController().getError() <= deadband) {
			output = 0;
		}
		SmartDashboard.putNumber("Output", output);
		drive.setDefaultThrottle(oi.getDriveAxisRight() + output, oi.getDriveAxisRight() - output); //Yes these should
		// both be right, it's driveStraight
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
	 * Log the drive data.
	 */
	@Override
	protected void execute() {
		drive.logData();
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
		System.out.println("NavXDriveStraight end");
		this.getPIDController().disable();
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("NavXDriveStraight interrupted!");
		this.getPIDController().disable();
	}
}
