package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Run the fixed motor but not the actuated. This is used to agitate balls in the bin.
 */
public class FixedInActuatedStop extends ReferencingCommand {

	/**
	 * The intake subsystem to execute this command on
	 */
	private Intake2017 intake;

	/**
	 * Default constructor.
	 *
	 * @param intake The intake subsystem to execute this command on.
	 */
	public FixedInActuatedStop(Intake2017 intake) {
		super(intake);
		this.intake = intake;
	}

	/**
	 * Set the fixed motor to go in slowly and the actuated motor to stop.
	 */
	@Override
	protected void execute() {
		intake.setFixedVictor(Intake2017.FixedIntakeMode.AGITATING);
		intake.setActuatedVictor(false);
	}

	/**
	 * Runs instantaneously.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}
}
