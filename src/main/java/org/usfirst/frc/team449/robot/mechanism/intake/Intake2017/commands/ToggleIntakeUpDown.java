package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Command for toggling the intake up and down
 */
public class ToggleIntakeUpDown extends ReferencingCommand {
	/**
	 * The intake subsystem to execute this command on
	 */
	Intake2017 intake2017;

	/**
	 * Construct a ToggleIntakeUpDown command
	 *
	 * @param intake2017 intake subsytem to execute this command on
	 */
	public ToggleIntakeUpDown(Intake2017 intake2017) {
		super(intake2017);
		requires(intake2017);
		this.intake2017 = intake2017;
		System.out.println("ToggleIntakeUpDown constructed");
	}

	/**
	 * Set the piston as appropriate
	 */
	@Override
	protected void execute() {
		intake2017.setPiston(intake2017.intakeUp ? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}
}
