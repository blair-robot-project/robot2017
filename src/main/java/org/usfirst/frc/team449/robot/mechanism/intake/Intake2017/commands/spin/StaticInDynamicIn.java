package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Run both intake motors. Used to pick up balls from the ground.
 */
public class StaticInDynamicIn extends ReferencingCommand {

	/**
	 * The intake subsystem to execute the command on
	 */
	private Intake2017 intake;

	public StaticInDynamicIn(Intake2017 intake) {
		super(intake);
		this.intake = intake;
	}

	@Override
	protected void initialize() {
		//TODO Stop hardcoding these
		intake.setFixedVictor(-0.7);
		intake.setActuatedVictor(1);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}
}
