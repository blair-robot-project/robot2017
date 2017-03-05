package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * Created by blairrobot on 1/10/17.
 */
public class AccelerateFlywheel extends ReferencingCommand {

	SingleFlywheelShooter flywheelShooter;

	public AccelerateFlywheel(MappedSubsystem subsystem, double timeout) {
		super(subsystem, timeout);
		flywheelShooter = (SingleFlywheelShooter) subsystem;
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		System.out.println("AccelerateFlywheel init");
	}

	@Override
	protected void execute() {
		flywheelShooter.logData(((SingleFlywheelShooter) subsystem).throttle*100.0);
		flywheelShooter.setDefaultSpeed(((SingleFlywheelShooter) subsystem).throttle);
		flywheelShooter.spinning = true;
		System.out.println("AccelerateFlywheel executed");
	}

	@Override
	protected boolean isFinished() {
		return true;
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
