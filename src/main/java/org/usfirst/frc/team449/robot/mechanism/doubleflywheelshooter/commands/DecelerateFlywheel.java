package org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.DoubleFlywheelShooter;

/**
 * Created by blairrobot on 1/10/17.
 */
public class DecelerateFlywheel extends ReferencingCommand{

	DoubleFlywheelShooter flywheelShooter;

	public DecelerateFlywheel(MappedSubsystem subsystem, double timeout) {
		super(subsystem, timeout);
		flywheelShooter = (DoubleFlywheelShooter) subsystem;
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		System.out.println("DecelerateFlywheel init");
	}

	@Override
	protected void execute() {
		flywheelShooter.setDefaultSpeed(0.0);
		flywheelShooter.spinning = false;
		System.out.println("DecelerateFlywheel executed");
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		System.out.println("DecelerateFlywheel end");
	}

	@Override
	protected void interrupted() {
		flywheelShooter.setDefaultSpeed(0);
		System.out.println("DecelerateFlywheel interrupted, stopping flywheel.");
	}
}
