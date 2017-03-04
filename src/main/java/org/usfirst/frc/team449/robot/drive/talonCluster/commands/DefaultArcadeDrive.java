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
	/**
	 * The OI giving the vel and turn stick values.
	 */
	public ArcadeOI oi;

	/**
	 * Whether or not we should be using the NavX to drive straight stably.
	 */
	private boolean drivingStraight;

	/**
	 * The velocity input from OI. Should be between -1 and 1.
	 */
	private double vel;

	/**
	 * The rotation input from OI. Should be between -1 and 1.
	 */
	private double rot;

	/**
	 * The talonClusterDrive this command is controlling.
	 */
	private TalonClusterDrive driveSubsystem;

	/**
	 * The maximum velocity for the robot to be at in order to switch to driveStraight, in degrees/sec
	 */
	private double maxAngularVel;
	/**
	 * The map of values
	 */
	ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map;

	/**
	 * Default constructor
	 * @param map The angle PID map containing PID and other tuning constants.
	 * @param drive The drive to execute this command on.
	 * @param oi The OI controlling the robot.
	 */
	public DefaultArcadeDrive(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, TalonClusterDrive drive,
	                          ArcadeOI oi) {
		//Assign stuff
		super(map, drive);
		maxAngularVel = map.getMaxAngularVel();
		this.oi = oi;
		this.map = map;
		driveSubsystem = drive;

		//Needs a requires because it's a default command.
		requires(drive);

		//Logging, but in Spanish.
		System.out.println("Drive Robot bueno");
	}

	/**
	 * Initialize PIDController and variables.
	 */
	@Override
	protected void initialize() {
		//Reset all values of the PIDController and enable it.
		this.getPIDController().reset();
		this.getPIDController().enable();
		System.out.println("DefaultArcadeDrive init.");

		//Initial assignment
		drivingStraight = false;
		vel = oi.getFwd();
		rot = oi.getRot();

		//starting from rest, we want to be in low gear so we can accelerate
		driveSubsystem.setLowGear(true);
	}

	/**
	 * Decide whether or not we should be in free drive or straight drive, and log data.
	 */
	@Override
	protected void execute() {
		//Auto-shifting
		driveSubsystem.autoShift();

		//Set vel and rot to what they should be.
		vel = oi.getFwd();
		rot = oi.getRot();

		//If we're driving straight but the driver tries to turn or overrides the NavX:
		if ((drivingStraight && rot != 0) || driveSubsystem.overrideNavX) {
			//Switch to free drive
			drivingStraight = false;
			System.out.println("Switching to free drive.");
		}
		//If we're free driving and the driver lets go of the turn stick:
		else if (!(drivingStraight) && rot == 0 && Math.abs(driveSubsystem.navx.getRate()) <= maxAngularVel) {
			//Switch to driving straight
			drivingStraight = true;
			//Set the setpoint to the current heading and reset the NavX
			this.getPIDController().reset();
			this.getPIDController().setSetpoint(subsystem.getGyroOutput());
			this.getPIDController().enable();
			System.out.println("Switching to DriveStraight.");
		}

		//Log data and stuff
		driveSubsystem.logData();
		SmartDashboard.putBoolean("driving straight?", drivingStraight);
		SmartDashboard.putNumber("Vel Axis", vel);
		SmartDashboard.putNumber("Rot axis", rot);
	}

	/**
	 * Run constantly because this is a defaultDrive
	 * @return false
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
		System.out.println("DefaultArcadeDrive End.");
	}

	/**
	 * Stop the motors and log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		System.out.println("DefaultArcadeDrive Interrupted! Stopping the robot.");
		driveSubsystem.setDefaultThrottle(0.0, 0.0);
	}

	/**
	 * Give the correct output to the motors based on whether we're in free drive or drive straight.
	 * @param output The output of the angular PID loop.
	 */
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
			//Set the output to 0 if we're within the deadband. Whether or not the deadband is enabled is dealt with
			// in PIDAngleCommand.
			if (Math.abs(this.getPIDController().getError()) < deadband) {
				output = 0;
			}
			//Log stuff
			SmartDashboard.putNumber("PID output", output);

			//Adjust the heading according to the PID output, it'll be positive if we want to go right.
			if (!map.getInverted()) {
				driveSubsystem.setDefaultThrottle(vel - output, vel + output);
			} else {
				driveSubsystem.setDefaultThrottle(vel + output, vel - output);
			}
		}
		//If we're free driving...
		else {
			//Set the throttle to normal arcade throttle.
			driveSubsystem.setDefaultThrottle(vel - rot, vel + rot);
		}
	}
}