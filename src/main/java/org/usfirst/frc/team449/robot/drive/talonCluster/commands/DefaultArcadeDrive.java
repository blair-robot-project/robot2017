package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.ArcadeOI;

/**
 * Drive with arcade drive setup, and when the driver isn't turning, use a NavX to stabilize the robot's alignment.
 */
public class DefaultArcadeDrive extends PIDAngleCommand {
	//The OI giving the vel and turn stick values.
	public ArcadeOI oi;

	//Whether or not we should be using the NavX to drive straight stably.
	private boolean drivingStraight;
	//The velocity input from OI. Should be between -1 and 1.
	private double vel;
	//The rotation input from OI. Should be between -1 and 1.
	private double rot;
	//The talonClusterDrive this command is controlling.
	private TalonClusterDrive driveSubsystem;

	public DefaultArcadeDrive(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, TalonClusterDrive drive, ArcadeOI oi) {
		super(map, drive);
		this.oi = oi;
		requires(drive);
		driveSubsystem = drive;
		System.out.println("Drive Robot bueno");
	}

	@Override
	protected void initialize() {
		//Reset all values of the PIDController and disable it.
		this.getPIDController().reset();
		this.getPIDController().enable();
		System.out.println("DefaultArcadeDrive init.");
		//Initial assignment
		drivingStraight = false;
		vel = oi.getFwd();
		rot = oi.getRot();
	}

	@Override
	protected void execute() {
		//Set vel and rot to what they should be.
		vel = oi.getFwd();
		rot = oi.getRot();

		//If we're driving straight but the driver tries to turn:
		if (drivingStraight && rot != 0) {
			//Switch to free drive
			drivingStraight = false;
			//Reset and disable the PID loop.
			//this.getPIDController().reset();
			System.out.println("Switching to free drive.");
		}
		//If we're free driving and the driver lets go of the turn stick:
		else if (!(drivingStraight) && rot == 0) {
			//Switch to driving straight
			drivingStraight = true;
			//Set the setpoint to the current heading
			this.getPIDController().reset();
			this.getPIDController().setSetpoint(subsystem.getGyroOutput());
			this.getPIDController().enable();
			//Enable the controller
			//this.getPIDController().enable();
			System.out.println("Switching to DriveStraight.");
		}

		//Log data and stuff
		driveSubsystem.logData();
		SmartDashboard.putBoolean("driving straight?", drivingStraight);
		SmartDashboard.putNumber("Vel Axis", vel);
		SmartDashboard.putNumber("Rot axis", rot);
	}

	@Override
	protected boolean isFinished() {
		//Runs constantly because this is a defaultDrive
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
		//If we're driving straight..
		if (drivingStraight) {
			//If we're using minimumOutput..
			if (minimumOutputEnabled) {
				//Set the output to the minimum if it's too small.
				if (output > 0 && output < minimumOutput) {
					output = minimumOutput;
				} else if (output < 0 && output > -minimumOutput) {
					output = -minimumOutput;
				}
			}
			//Set the output to 0 if we're within the deadband. Whether or not the deadband is enabled is dealt with in PIDAngleCommand.
			if (Math.abs(this.getPIDController().getError()) < deadband) {
				output = 0;
			}
			//Log stuff
			SmartDashboard.putNumber("PID output", output);

			//Adjust the heading according to the PID output, it'll be positive if we want to go right.
			driveSubsystem.setDefaultThrottle(vel - output, vel + output);
		}
		//If we're free driving...
		else {
			//Set the throttle to normal arcade throttle.
			driveSubsystem.setDefaultThrottle(vel - rot, vel + rot);
		}
	}
}
