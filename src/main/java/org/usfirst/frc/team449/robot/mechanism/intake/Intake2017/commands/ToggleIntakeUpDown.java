package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Created by blairrobot on 2/4/17.
 */
public class ToggleIntakeUpDown extends ReferencingCommandGroup {

	Intake2017 intake2017;

	public ToggleIntakeUpDown(Intake2017 intake2017) {
		super(intake2017);
		requires(intake2017);
		this.intake2017 = intake2017;
		System.out.println("ToggleIntakeUpDown constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("ToggleIntakeUpDown init");
	}

	@Override
	protected void execute() {
		intake2017.setPiston(intake2017.intakeUp? DoubleSolenoid.Value.kForward : DoubleSolenoid.Value.kReverse);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("ToggleIntakeUpDown end");
	}

	@Override
	protected void interrupted() {
		System.out.println("ToggleIntakeUpDown interrupted.");
	}

}
