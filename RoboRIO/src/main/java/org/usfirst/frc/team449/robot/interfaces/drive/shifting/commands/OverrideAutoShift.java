package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Override or unoverride whether we're autoshifting. Used to stay in low gear for pushing matches and more!
 */
public class OverrideAutoShift extends Command {

	/**
	 * Whether or not to override.
	 */
	private boolean override;

	private ShiftingDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param drive           The drive subsystem to execute this command on.
	 * @param override        Whether or not to override autoshifting.
	 */
	public OverrideAutoShift(ShiftingDrive drive, boolean override) {
		subsystem = drive;
		this.override = override;
	}

	/**
	 * Log on initialization
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("OverrideAutoShift init", this.getClass());
	}

	/**
	 * Override autoshifting.
	 */
	@Override
	protected void execute() {
		//Set whether or not we're overriding
		subsystem.setOverrideAutoshift(override);
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
	 * Log when this command ends.
	 */
	@Override
	protected void end() {
		Logger.addEvent("OverrideAutoShift end", this.getClass());
	}

	/**
	 * Log when interrupted
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("OverrideAutoShift Interrupted!", this.getClass());
	}
}

