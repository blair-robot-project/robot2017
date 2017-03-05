package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Created by ryant on 2017-02-19.
 */
public class SIDI extends ReferencingCommand {

	Intake2017 intake;

	public SIDI(Intake2017 intake) {
		super(intake);
		this.intake = intake;
	}

	@Override
	protected void initialize() {
		intake.setFixedVictor(-0.7);
		intake.setActuatedVictor(1);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}
}
