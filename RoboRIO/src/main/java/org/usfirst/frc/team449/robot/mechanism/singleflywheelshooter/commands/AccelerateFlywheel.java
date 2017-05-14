package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * ReferencingCommand for running the flywheel
 */
public class AccelerateFlywheel extends ReferencingCommand {
	/**
	 * Flywheel subsystem to execute this command on
	 */
	private SingleFlywheelShooter flywheelShooter;

	/**
	 * Construct an AccelerateFLywheel command
	 *
	 * @param subsystem shooter to execute this command on
	 */
	public AccelerateFlywheel(MappedSubsystem subsystem) {
		super(subsystem);
		flywheelShooter = (SingleFlywheelShooter) subsystem;
		requires(subsystem);
	}

	/**
	 * Init the command in the stdout log
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("AccelerateFlywheel init", this.getClass());
	}

	/**
	 * Set velocity setpoint and log to file
	 */
	@Override
	protected void execute() {
		flywheelShooter.talon.canTalon.enable();
		flywheelShooter.logData(((SingleFlywheelShooter) subsystem).throttle);
		flywheelShooter.setDefaultSpeed(((SingleFlywheelShooter) subsystem).throttle);
		flywheelShooter.spinning = true;
		Logger.addEvent("AccelerateFlywheel executed", this.getClass());
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
		Logger.addEvent("AccelerateFlywheel end", this.getClass());
	}

	/**
	 * Stop the flywheel if the command is interrupted and log to stdout
	 */
	@Override
	protected void interrupted() {
		flywheelShooter.setDefaultSpeed(0);
		Logger.addEvent("AccelerateFlywheel interrupted, stopping flywheel.", this.getClass());
	}
}
