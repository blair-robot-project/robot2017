package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;

/**
 * Created by Noah Gleason on 1/30/2017.
 */
public class DefaultArcadeDrive extends PIDAngleCommand{
	public OI2017ArcadeGamepad oi;
	
	private boolean drivingStraight;
	private double vel;
	private double rot;
	private TalonClusterDrive driveSubsystem;

	public DefaultArcadeDrive(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, TalonClusterDrive drive, OI2017ArcadeGamepad oi) {
		super(map, drive);
		this.oi = oi;
		requires(drive);
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		this.getPIDController().reset();
		System.out.println("DefaultArcadeDrive init.");
		drivingStraight = false;
		vel = oi.getFwd();
		driveSubsystem.setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void execute() {
		if (drivingStraight && oi.getRot() != 0){
			drivingStraight = false;
			//Reset disables it too.
			this.getPIDController().reset();
			System.out.println("Switching to free drive.");
		} else if (!(drivingStraight) && oi.getRot() == 0){
			drivingStraight = true;
			this.getPIDController().setSetpoint(subsystem.getGyroOutput());
			this.getPIDController().enable();
			System.out.println("Switching to DriveStraight.");
		}
		driveSubsystem.logData();
		vel = oi.getFwd();
		rot = oi.getRot();
		SmartDashboard.putBoolean("driving straight?", drivingStraight);
		SmartDashboard.putNumber("Vel Axis", vel);
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
		driveSubsystem.setDefaultThrottle(0.0, 0.0);
	}

	@Override
	protected void usePIDOutput(double output) {
		if (drivingStraight) {
			if (minimumOutputEnabled) {
				//Set the output to the minimum if it's too small.
				if (output > 0 && output < minimumOutput) {
					output = minimumOutput;
				} else if (output < 0 && output > -minimumOutput) {
					output = -minimumOutput;
				}
			}
			if (Math.abs(this.getPIDController().getError()) < deadband) {
				output = 0;
			}
			SmartDashboard.putNumber("PID output", output);
			driveSubsystem.setDefaultThrottle(vel + output, vel - output);
		} else {
			driveSubsystem.setDefaultThrottle(vel + rot, vel - rot);
		}
	}
}
