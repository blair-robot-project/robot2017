package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.spin;

import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Stop both intake motors.
 */
public class FixedStopActuatedStop extends ReferencingCommand {

	/**
	 * The intake subsystem to execute this command on
	 */
	private Intake2017 intake;

	/**
	 * Default constructor.
	 *
	 * @param intake The intake subsystem to execute this command on.
	 */
	public FixedStopActuatedStop(Intake2017 intake) {
		super(intake);
		this.intake = intake;
	}

	/**
	 * Set the fixed and actuated motors to stop.
	 */
	@Override
	protected void execute() {
		intake.setActuatedVictor(false);
		intake.setFixedVictor(Intake2017.FixedIntakeMode.OFF);
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
