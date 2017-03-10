package org.usfirst.frc.team449.robot.drive.meccanum.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.meccanum.MeccanumDrive;

/**
 * Created by sam on 1/27/17.
 */
public class ToggleStrafe extends ReferencingCommand {

	public ToggleStrafe(MeccanumDrive drive) {
		super(drive);
		requires(subsystem);
	}

	@Override
	protected void initialize() {
		((MeccanumDrive) subsystem).toggleStrafe();
	}

	@Override
	protected void execute() {
		//Do nothing
	}

	@Override
	protected boolean isFinished() {
		return true;
	}
}
