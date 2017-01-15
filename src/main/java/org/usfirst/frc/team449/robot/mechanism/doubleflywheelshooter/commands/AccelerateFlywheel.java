package org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.DoubleFlywheelShooter;

/**
 * Created by blairrobot on 1/10/17.
 */
public class AccelerateFlywheel extends ReferencingCommand{

	DoubleFlywheelShooter flywheelShooter;

	public AccelerateFlywheel(MappedSubsystem subsystem, double timeout) {
		super(subsystem, timeout);
		flywheelShooter = (DoubleFlywheelShooter) subsystem;
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		System.out.println("AccelerateFlywheel init");
	}

	@Override
	protected void execute() {
		flywheelShooter.logData(65);
		flywheelShooter.setDefaultSpeed(1);
		flywheelShooter.spinning = true;
		System.out.println("AccelerateFlywheel executed");
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		System.out.println("AccelerateFlywheel end");
	}

	@Override
	protected void interrupted() {
		flywheelShooter.setDefaultSpeed(0);
		System.out.println("AccelerateFlywheel interrupted, stopping flywheel.");
	}
}
