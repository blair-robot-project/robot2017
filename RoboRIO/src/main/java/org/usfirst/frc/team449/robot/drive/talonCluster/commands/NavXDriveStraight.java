package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.util.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.oi.TankOI;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands.PIDAngleCommand;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Drives straight using the NavX gyro to keep a constant alignment.
 */
public class NavXDriveStraight extends PIDAngleCommand {

	private TankOI oi;
	private UnidirectionalDrive drive;
	private boolean useLeft;

	public NavXDriveStraight(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, UnidirectionalDrive drive,
	                         TankOI oi, boolean useLeft) {
		super(map, (NavxSubsystem) drive);
		this.oi = oi;
		this.drive = drive;
		this.useLeft = useLeft;
		requires((Subsystem) drive);
	}

	/**
	 * Give output to the drive based on the out of the PID loop.
	 *
	 * @param output the value the PID loop calculated
	 */
	@Override
	protected void usePIDOutput(double output) {
		output = processPIDOutput(output);

		SmartDashboard.putNumber("NavXDriveStraight PID output", output);
		double throttle;
		if (useLeft) {
			throttle = oi.getLeftThrottle();
		} else {
			throttle = oi.getRightThrottle();
		}

		drive.setOutput(throttle - output, throttle + output);
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
		Logger.addEvent("NavXDriveStraight end", this.getClass());
		this.getPIDController().disable();
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("NavXDriveStraight interrupted!", this.getClass());
		this.getPIDController().disable();
	}
}
