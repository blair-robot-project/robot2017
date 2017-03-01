package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Created by blairrobot on 1/28/17.
 */
public class ToggleIntaking extends ReferencingCommand {

	private Intake2017 intake2017;

	public ToggleIntaking(Intake2017 intake2017) {
		super(intake2017);
		requires(intake2017);
		this.intake2017 = intake2017;
		System.out.println("ToggleIntaking constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("ToggleIntaking init");
	}

	@Override
	protected void execute() {
		if (intake2017.isIntaking) {
			intake2017.setActuatedVictor(0);
			intake2017.setFixedVictor(0);
		} else {
			intake2017.setActuatedVictor(1);
			intake2017.setFixedVictor(-0.5);
		}
		intake2017.setIntaking(!intake2017.isIntaking);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("ToggleIntaking end");
	}

	@Override
	protected void interrupted() {
		intake2017.setActuatedVictor(0);
		intake2017.setFixedVictor(0);
		System.out.println("ToggleIntaking interrupted, stopping intake2017.");
	}

}
