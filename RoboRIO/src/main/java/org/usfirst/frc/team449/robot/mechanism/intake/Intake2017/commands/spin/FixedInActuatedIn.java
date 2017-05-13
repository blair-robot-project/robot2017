package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Run both intake motors. Used to pick up balls from the ground.
 */
public class FixedInActuatedIn extends ReferencingCommand {

	/**
	 * The intake subsystem to execute this command on
	 */
	private Intake2017 intake;

	/**
	 * Default constructor.
	 *
	 * @param intake The intake subsystem to execute this command on.
	 */
	public FixedInActuatedIn(Intake2017 intake) {
		super(intake);
		this.intake = intake;
	}

	/**
	 * Set the fixed and actuated motors to go in.
	 */
	@Override
	protected void execute() {
		intake.setFixedVictor(Intake2017.FixedIntakeMode.INTAKING);
		intake.setActuatedVictor(true);
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
