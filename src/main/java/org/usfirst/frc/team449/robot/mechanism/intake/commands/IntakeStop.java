package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake;

/**
 * Created by blairrobot on 1/28/17.
 */
public class IntakeStop  extends ReferencingCommand {

	Intake intake;

	public IntakeStop(Intake intake) {
		super(intake);
		requires(intake);
		this.intake = intake;
		System.out.println("IntakeStop constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeStop init");
	}

	@Override
	protected void execute() {
		intake.setPercentVbus(0);
		intake.setIntaking(false);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		intake.setPercentVbus(0);
		System.out.println("IntakeStop end");
	}

	@Override
	protected void interrupted() {
		intake.setPercentVbus(0);
		System.out.println("IntakeStop interrupted, stopping intake.");
	}

}