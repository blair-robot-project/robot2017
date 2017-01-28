package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake;

/**
 * Created by Justin on 1/28/2017.
 */
public class IntakeIn extends ReferencingCommand {

	Intake intake;

	public IntakeIn(Intake intake) {
		super(intake);
		requires(intake);
		this.intake = intake;
		System.out.println("Intake constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("Intake init");
	}

	@Override
	protected void execute() {
		intake.setPercentVbus(1);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}

	@Override
	protected void end() {
		intake.setPercentVbus(0);
		System.out.println("Intake end");
	}

	@Override
	protected void interrupted() {
		intake.setPercentVbus(0);
		System.out.println("Intake interrupted, stopping intake.");
	}

}
