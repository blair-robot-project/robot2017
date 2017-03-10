package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * Command for toggling whether the shooter is running
 */
public class ToggleShooter extends ReferencingCommand {
	/**
	 * Flywheel subsystem to execute this command on
	 */
	private SingleFlywheelShooter flywheelShooter;

	/**
	 * Construct an AccelerateFLywheel command
	 *
	 * @param subsystem shooter to execute this command on
	 */
	public ToggleShooter(MappedSubsystem subsystem) {
		super(subsystem);
		flywheelShooter = (SingleFlywheelShooter) subsystem;
		requires(subsystem);
	}

	/**
	 * Toggle the shooter
	 */
	@Override
	protected void initialize() {
		if (!flywheelShooter.spinning) {
			flywheelShooter.setDefaultSpeed(((SingleFlywheelShooter) subsystem).throttle);
			flywheelShooter.spinning = true;
		} else {
			flywheelShooter.setDefaultSpeed(0);
			flywheelShooter.spinning = false;
		}
	}

	/**
	 * Log to file
	 */
	@Override
	protected void execute() {
		flywheelShooter.logData(((SingleFlywheelShooter) subsystem).throttle * 100.0);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Stop the flywheel if the command is interrupted
	 */
	@Override
	protected void interrupted() {
		flywheelShooter.setDefaultSpeed(0);
	}
}
