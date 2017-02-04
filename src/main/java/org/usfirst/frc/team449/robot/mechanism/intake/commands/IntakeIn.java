package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

/**
 * Created by Justin on 1/28/2017.
 */
public class IntakeIn extends ReferencingCommand {

	Intake2017 intake2017;

	public IntakeIn(Intake2017 intake2017) {
		super(intake2017);
		requires(intake2017);
		this.intake2017 = intake2017;
		System.out.println("IntakeIn constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeIn init");
	}

	@Override
	protected void execute() {
		intake2017.setPercentVbus(1);
		intake2017.setIntaking(true);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		intake2017.setPercentVbus(0);
		System.out.println("IntakeIn end");
	}

	@Override
	protected void interrupted() {
		intake2017.setPercentVbus(0);
		System.out.println("IntakeIn interrupted, stopping intake2017.");
	}

}
