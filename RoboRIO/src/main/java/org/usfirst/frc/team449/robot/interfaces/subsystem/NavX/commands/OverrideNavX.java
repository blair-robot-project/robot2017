package org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.NavX.NavxSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Set whether or not to override the NavX.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.StringIdGenerator.class)
public class OverrideNavX extends Command {

	/**
	 * Whether or not to override the NavX.
	 */
	private boolean override;

	/**
	 * The subsystem to execute this command on.
	 */
	private NavxSubsystem subsystem;

	/**
	 * Default constructor.
	 *
	 * @param subsystem The subsystem to execute this command on
	 * @param override  Whether or not to override the NavX.
	 */
	public OverrideNavX(NavxSubsystem subsystem, boolean override) {
		this.override = override;
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("OverrideNavX init", this.getClass());
	}

	/**
	 * Set whether or not we're overriding the NavX
	 */
	@Override
	protected void execute() {
		subsystem.setOverrideNavX(override);
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
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("OverrideNavX end", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("OverrideNavX Interrupted!", this.getClass());
	}
}

