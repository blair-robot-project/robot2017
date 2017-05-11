package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Run the static motor but not the dynamic. This is used to agitate balls in the bin.
 */
public class StaticInDynamicStop extends ReferencingCommand {

	/**
	 * The intake subsystem to execute this command on
	 */
	private Intake2017 intake;

	/**
	 * Default constructor.
	 * @param intake The intake subsystem to execute this command on.
	 */
	public StaticInDynamicStop(Intake2017 intake) {
		super(intake);
		this.intake = intake;
	}

	/**
	 * Set the fixed motor to go in slowly and the actuated motor to stop.
	 */
	@Override
	protected void execute() {
		//TODO Stop hardcoding these
		intake.setFixedVictor(-0.3);
		intake.setActuatedVictor(0);
	}

	/**
	 * Runs instantaneously.
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}
}
