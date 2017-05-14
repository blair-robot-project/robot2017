package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.util.Logger;

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
		Logger.addEvent("DecelerateFlywheel init", this.getClass());
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
		Logger.addEvent("DecelerateFlywheel executed", this.getClass());
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
		Logger.addEvent("DecelerateFlywheel end", this.getClass());
	}

	/**
	 * Stop the flywheel if the command is interrupted and log to stdout
	 */
	@Override
	protected void interrupted() {
		flywheelShooter.setDefaultSpeed(0);
		Logger.addEvent("DecelerateFlywheel interrupted, stopping flywheel.", this.getClass());
	}
}
