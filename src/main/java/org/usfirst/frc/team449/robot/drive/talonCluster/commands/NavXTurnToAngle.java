package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Created by blairrobot on 1/14/17.
 */
public class NavXTurnToAngle extends PIDAngleCommand{

	private TalonClusterDrive drive;
	private double sp;

	public NavXTurnToAngle(double p, double i, double d, double absoluteTolerance, double sp, TalonClusterDrive drive){
		super(p, i, d, absoluteTolerance);
		this.drive = drive;
		this.sp = sp;
		requires(drive);
	}
	@Override
	protected double returnPIDInput() {
		return drive.navx.pidGet();
	}

	@Override
	protected void usePIDOutput(double output) {
		//TODO replace with deadband.
		if (output > 0)
			output = Math.max(output, 0.05);
		else if (output < 0)
			output = Math.min(output, -0.05);
		drive.setDefaultThrottle(output, -output);
	}

	@Override
	protected void initialize() {
		this.setSetpoint(sp);
		this.getPIDController().enable();
	}

	@Override
	protected void execute() {
		drive.logData();
	}

	@Override
	protected boolean isFinished() {
		return this.getPIDController().onTarget();
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
