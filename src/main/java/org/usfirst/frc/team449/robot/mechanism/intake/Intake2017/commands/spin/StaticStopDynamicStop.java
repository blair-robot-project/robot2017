package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Stop both intake motors.
 */
public class StaticStopDynamicStop extends ReferencingCommand {

	/**
	 * The intake subsystem to execute the command on
	 */
	private Intake2017 intake;

	public StaticStopDynamicStop(Intake2017 intake) {
		super(intake);
		this.intake = intake;
	}

	@Override
	protected void initialize() {
		intake.setActuatedVictor(0);
		intake.setFixedVictor(0);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}
}
