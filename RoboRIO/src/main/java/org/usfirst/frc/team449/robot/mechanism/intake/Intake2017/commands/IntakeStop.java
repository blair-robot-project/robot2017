package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Created by blairrobot on 1/28/17.
 */
@Deprecated
public class IntakeStop extends ReferencingCommand {

	Intake2017 intake2017;

	public IntakeStop(Intake2017 intake2017) {
		super(intake2017);
		requires(intake2017);
		this.intake2017 = intake2017;
		System.out.println("IntakeStop constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeStop init");
	}

	@Override
	protected void execute() {
		intake2017.setActuatedVictor(0);
		intake2017.setFixedVictor(0);
		intake2017.setIntaking(false);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("IntakeStop end");
	}

	@Override
	protected void interrupted() {
		intake2017.setActuatedVictor(0);
		intake2017.setFixedVictor(0);
		System.out.println("IntakeStop interrupted, stopping intake2017.");
	}

}