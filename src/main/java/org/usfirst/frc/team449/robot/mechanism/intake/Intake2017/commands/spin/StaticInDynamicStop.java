package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Run the static motor but not the dynamic. This is used to agitate balls in the bin.
 */
public class StaticInDynamicStop extends ReferencingCommand {

	/**
	 * The intake subsystem to execute the command on
	 */
	private Intake2017 intake;

	public StaticInDynamicStop(Intake2017 intake) {
		super(intake);
		this.intake = intake;
	}

	@Override
	protected void initialize() {
		//TODO Stop hardcoding these
		intake.setFixedVictor(-0.3);
		intake.setActuatedVictor(0);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}
}
