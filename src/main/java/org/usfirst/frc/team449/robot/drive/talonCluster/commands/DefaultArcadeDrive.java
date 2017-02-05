package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.components.NavxSubsystem;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;

/**
 * Created by Noah Gleason on 1/30/2017.
 */
public class DefaultArcadeDrive extends PIDAngleCommand{
	public OI2017ArcadeGamepad oi;

	private double leftThrottle;
	private double rightThrottle;
	private boolean drivingStraight;
	private double vel;

	public DefaultArcadeDrive(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, TalonClusterDrive drive, OI2017ArcadeGamepad oi) {
		super(map, drive);
		this.oi = oi;
		requires(drive);
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		this.getPIDController().setSetpoint(subsystem.getGyroOutput());
		this.getPIDController().disable();
		System.out.println("DefaultArcadeDrive init.");
		drivingStraight = true;
		vel = oi.getVelAxis();
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void execute() {
		/*
		if (drivingStraight && oi.getTurnAxis() != 0){
			drivingStraight = false;
			this.getPIDController().disable();
			System.out.println("Switching to free drive.");
		} else if (!drivingStraight && oi.getTurnAxis() == 0){
			drivingStraight = true;
			this.getPIDController().setSetpoint(subsystem.getGyroOutput());
			this.getPIDController().enable();
			System.out.println("Switching to DriveStraight.");
		}*/

		vel = oi.getVelAxis();
		SmartDashboard.putBoolean("drivingStraight", drivingStraight);
		SmartDashboard.putNumber("velAxis", vel);

		//if (!drivingStraight) {
			rightThrottle = oi.getDriveAxisRight();
			leftThrottle = oi.getDriveAxisLeft();
			((TalonClusterDrive) subsystem).logData();
			SmartDashboard.putNumber("right drive axis", rightThrottle);
			SmartDashboard.putNumber("left drive axis", leftThrottle);
			((TalonClusterDrive) subsystem).setDefaultThrottle(leftThrottle, rightThrottle);
		//}
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		System.out.println("DefaultArcadeDrive End.");
	}

	@Override
	protected void interrupted() {
		System.out.println("DefaultArcadeDrive Interrupted! Stopping the robot.");
		((TalonClusterDrive) subsystem).setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void usePIDOutput(double output) {
		if (minimumOutputEnabled) {
			//Set the output to the minimum if it's too small.
			if (output > 0 && output < minimumOutput) {
				output = minimumOutput;
			} else if (output < 0 && output > -minimumOutput) {
				output = -minimumOutput;
			}
		}
		if (Math.abs(this.getPIDController().getError()) < deadband){
			output = 0;
		}
		SmartDashboard.putNumber("PID output", output);
		((TalonClusterDrive) subsystem).setDefaultThrottle(vel+output, vel-output);
	}
}
