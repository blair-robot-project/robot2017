package org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands;

import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;

/**
 * Override or unoverride whether we're autoshifting. Used to stay in low gear for pushing matches and more!
 */
public class ToggleOverrideAutoShift extends Command {

	private ShiftingDrive subsystem;

	/**
	 * Default constructor
	 *
	 * @param drive           The drive subsystem to execute this command on.
	 */
	public ToggleOverrideAutoShift(ShiftingDrive drive) {
		subsystem = drive;
	}

	/**
	 * Log on initialization
	 */
	@Override
	protected void initialize() {
		System.out.println("OverrideAutoShift init");
	}

	/**
	 * Override autoshifting.
	 */
	@Override
	protected void execute() {
		//Set whether or not we're overriding
		subsystem.setOverrideAutoshift(!subsystem.getOverrideAutoshift());
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
		System.out.println("OverrideAutoShift end");
	}

	/**
	 * Log when interrupted
	 */
	@Override
	protected void interrupted() {
		System.out.println("OverrideAutoShift Interrupted!");
	}
}

