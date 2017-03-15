package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * ReferencingCommand for stopping the flywheel
 */
public class DecelerateFlywheel extends ReferencingCommand {
	/**
	 * Flywheel subsystem to execute this command on
	 */
	private SingleFlywheelShooter flywheelShooter;

	//TODO Either implement or remove timeout.
	/**
	 * Construct a DecelerateFLywheel command
	 *
	 * @param subsystem shooter to execute this command on
	 * @param timeout   command timeout (doesn't actually work)
	 */
	public DecelerateFlywheel(MappedSubsystem subsystem, double timeout) {
		super(subsystem, timeout);
		flywheelShooter = (SingleFlywheelShooter) subsystem;
		requires(subsystem);
	}

	/**
	 * Init the command in the stdout log
	 */
	@Override
	protected void initialize() {
		System.out.println("DecelerateFlywheel init");
	}

	/**
	 * Set velocity setpoint and log to file
	 */
	@Override
	protected void execute() {
		flywheelShooter.logData(0.0);
		flywheelShooter.setDefaultSpeed(0.0);
		flywheelShooter.spinning = false;
		System.out.println("DecelerateFlywheel executed");
	}

	//TODO make AccelerateFlywheel and DecelerateFlywheel consistent here.
	/**
	 * Runs constantly in order to log data.
	 *
	 * @return false
	 */
	@Override
	protected boolean isFinished() {
		return false;
	}

	/**
	 * Log that the command has ended in the stdout log
	 */
	@Override
	protected void end() {
		System.out.println("DecelerateFlywheel end");
	}

	/**
	 * Stop the flywheel if the command is interrupted and log to stdout
	 */
	@Override
	protected void interrupted() {
		flywheelShooter.setDefaultSpeed(0);
		System.out.println("DecelerateFlywheel interrupted, stopping flywheel.");
	}
}
