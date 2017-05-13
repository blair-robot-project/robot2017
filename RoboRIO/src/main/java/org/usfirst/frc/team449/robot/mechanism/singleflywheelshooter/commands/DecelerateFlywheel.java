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


	/**
	 * Construct a DecelerateFlywheel command
	 *
	 * @param subsystem shooter to execute this command on
	 */
	public DecelerateFlywheel(MappedSubsystem subsystem) {
		super(subsystem);
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
		flywheelShooter.talon.canTalon.disable();
		System.out.println("DecelerateFlywheel executed");
	}

	/**
	 * Finish immediately because this is a state-change command.
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
