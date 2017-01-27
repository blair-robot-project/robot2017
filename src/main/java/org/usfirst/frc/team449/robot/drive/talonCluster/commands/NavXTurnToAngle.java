package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Turns to a specified angle, relative to the angle the NavX was at when the robot was turned on.
 */
public class NavXTurnToAngle extends PIDAngleCommand {

	private TalonClusterDrive drive;
	private double sp;
	private long timeout;
	private long startTime;
	//private double deadband;

	/**
	 * Default constructor.
	 *
	 * @param map   An turnPID map with PID values, an absolute tolerance, and minimum output.
	 * @param sp    The setpoint, in degrees from 180 to -180.
	 * @param drive The drive subsystem whose motors this is controlling.
	 */
	public NavXTurnToAngle(ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID map, double sp, TalonClusterDrive drive, double timeout) {
		super(map, drive);
		this.drive = drive;
		this.sp = sp;
		this.timeout = (long) (timeout * 1000);
		requires(drive);
	}

	@Override
	protected void usePIDOutput(double output) {
		SmartDashboard.putNumber("Preprocessed output", output);
		SmartDashboard.putNumber("Setpoint", this.getSetpoint());
		if (minimumOutputEnabled) {
			//Set the output to the minimum if it's too small.
			if (output > 0 && output < minimumOutput)
				output = minimumOutput;
			else if (output < 0 && output > -minimumOutput)
				output = -minimumOutput;
			else if (Math.abs(this.getPIDController().getAvgError()) < deadband)
				output = 0;
		}
		//Which one of these is negative may be different from robot to robot, we don't know.
		SmartDashboard.putNumber("Processed output", output);
		drive.setDefaultThrottle(output, -output);
	}

	@Override
	protected void initialize() {
		this.startTime = System.currentTimeMillis();
		this.setSetpoint(sp);
		//Make sure to enable the controller!
		this.getPIDController().enable();
	}

	@Override
	protected void execute() {
		//Just logging, nothing actually gets done.
		drive.logData();
		SmartDashboard.putBoolean("onTarget", this.getPIDController().onTarget());
		SmartDashboard.putNumber("Avg Navx Error", this.getPIDController().getAvgError());
	}

	@Override
	protected boolean isFinished() {
		//This method is crap and sometimes never terminates because of floating point errors, so we have a timeout
		return this.getPIDController().onTarget() || System.currentTimeMillis() - startTime > timeout;
		//return false;
	}

	@Override
	protected void end() {
		System.out.println("NavXTurnToAngle end.");
		this.getPIDController().disable();
	}

	@Override
	protected void interrupted() {
		System.out.println("NavXTurnToAngle interrupted!");
		this.getPIDController().disable();
	}
}
