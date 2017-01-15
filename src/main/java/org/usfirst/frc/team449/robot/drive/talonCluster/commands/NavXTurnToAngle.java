package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import maps.org.usfirst.frc.team449.robot.components.AnglePIDMap;
import org.usfirst.frc.team449.robot.components.NavxSubsystem;
import org.usfirst.frc.team449.robot.components.PIDAngleCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Created by blairrobot on 1/14/17.
 */
public class NavXTurnToAngle extends PIDAngleCommand{

	private TalonClusterDrive drive;
	private double sp;

	public NavXTurnToAngle(AnglePIDMap.AnglePID map, double sp, TalonClusterDrive drive){
		super(map, drive);
		this.drive = drive;
		this.sp = sp;
		requires(drive);
	}

	@Override
	protected void usePIDOutput(double output) {
		if (minimumOutputEnabled) {
			if (output > 0 && output < minimumOutput)
				output = minimumOutput;
			else if (output < 0 && output > -minimumOutput)
				output = -minimumOutput;
		}
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
