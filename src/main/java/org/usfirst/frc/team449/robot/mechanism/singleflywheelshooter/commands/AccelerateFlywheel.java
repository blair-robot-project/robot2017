package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * ReferencingCommand for running the flywheel
 */
public class AccelerateFlywheel extends ReferencingCommand {
	/**
	 * Flywheel subsystem to execute the command on
	 */
	SingleFlywheelShooter flywheelShooter;

	/**
	 * Construct an AccelerateFLywheel command
	 *
	 * @param subsystem shooter to execute the command on
	 * @param timeout   command timeout (doesn't actually work)
	 */
	public AccelerateFlywheel(MappedSubsystem subsystem, double timeout) {
		super(subsystem, timeout);
		flywheelShooter = (SingleFlywheelShooter) subsystem;
		requires(subsystem);
	}

	/**
	 * Init the command in the stdout log
	 */
	@Override
	protected void initialize() {
		System.out.println("AccelerateFlywheel init");
	}

	/**
	 * Set velocity setpoint and log to file
	 */
	@Override
	protected void execute() {
		flywheelShooter.logData(((SingleFlywheelShooter) subsystem).throttle * 100.0);
		flywheelShooter.setDefaultSpeed(((SingleFlywheelShooter) subsystem).throttle);
		flywheelShooter.spinning = true;
		System.out.println("AccelerateFlywheel executed");
	}

	/**
	 * Finish the command instantly, as the setpoint has already been set
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Log that the command has ended in the stdout log
	 */
	@Override
	protected void end() {
		System.out.println("AccelerateFlywheel end");
	}

	/**
	 * Stop the flywheel if the command is interrupted and log to stdout
	 */
	@Override
	protected void interrupted() {
		flywheelShooter.setDefaultSpeed(0);
		System.out.println("AccelerateFlywheel interrupted, stopping flywheel.");
	}
}
