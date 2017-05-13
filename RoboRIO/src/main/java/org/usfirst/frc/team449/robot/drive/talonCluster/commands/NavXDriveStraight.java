package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.TankOI;
import org.usfirst.frc.team449.robot.util.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Drives straight using the NavX gyro to keep a constant alignment.
 */
public class NavXDriveStraight extends PIDAngleCommand {

	private TankOI oi;
	private TalonClusterDrive drive;
	private boolean useLeft;
	private boolean inverted;

	public NavXDriveStraight(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, TalonClusterDrive drive,
	                         TankOI oi, boolean useLeft) {
		super(map, drive);
		this.oi = oi;
		this.drive = drive;
		this.useLeft = useLeft;
		this.inverted = map.getInverted();
		requires(drive);
	}

	/**
	 * Give output to the drive based on the out of the PID loop.
	 *
	 * @param output the value the PID loop calculated
	 */
	@Override
	protected void usePIDOutput(double output) {
		//If we're using minimumOutput..
		if (minimumOutputEnabled) {
			//Set the output to the minimum if it's too small.
			if (output > 0 && output < minimumOutput) {
				output = minimumOutput;
			} else if (output < 0 && output > -minimumOutput) {
				output = -minimumOutput;
			}
		}
		//Set the output to 0 if we're within the deadband. Whether or not the deadband is enabled is dealt with
		// in PIDAngleCommand.
		if (Math.abs(this.getPIDController().getError()) < deadband) {
			output = 0;
		}
		SmartDashboard.putNumber("NavXDriveStraight PID output", output);
		double throttle;
		if (useLeft){
			throttle = oi.getLeftThrottle();
		} else {
			throttle = oi.getRightThrottle();
		}

		if (inverted){
			drive.setDefaultThrottle(throttle + output, throttle - output);
		} else {
			drive.setDefaultThrottle(throttle - output, throttle + output);
		}
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
