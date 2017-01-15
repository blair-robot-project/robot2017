package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
		if (minimumOutputEnabled && this.getPIDController().getError()*3/4 > tolerance) { //Can't have tolerance be same as deadband because floating-point errors suck
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
		SmartDashboard.putBoolean("onTarget", this.getPIDController().onTarget());
		SmartDashboard.putNumber("Avg Navx Error", this.getPIDController().getAvgError());
	}

	@Override
	protected boolean isFinished() {
		return this.getPIDController().onTarget();
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
