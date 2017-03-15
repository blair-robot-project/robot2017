package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;

/**
 * Override or unoverride whether we're autoshifting. Used to stay in low gear for pushing matches and more!
 */
public class OverrideAutoShift extends ReferencingCommand {

	/**
	 * Whether or not to override.
	 */
	private boolean override;

	/**
	 * Whether to switch to low or high gear before overriding.
	 */
	private boolean switchToLowGear;

	/**
	 * Default constructor
	 * @param drive The drive subsystem to execute this command on.
	 * @param override Whether or not to override autoshifting.
	 * @param switchToLowGear Whether to switch to low gear. If false, switch to high gear.
	 */
	public OverrideAutoShift(TalonClusterDrive drive, boolean override, boolean switchToLowGear) {
		super(drive);
		requires(subsystem);
		this.override = override;
		this.switchToLowGear = switchToLowGear;
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
		((TalonClusterDrive) subsystem).overrideAutoShift = override;
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

