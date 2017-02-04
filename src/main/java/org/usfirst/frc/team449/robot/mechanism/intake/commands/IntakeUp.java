package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

/**
 * Created by Justin on 1/28/2017.
 */
public class IntakeUp extends ReferencingCommand {

	Intake2017 intake2017;

	public IntakeUp(Intake2017 intake2017) {
		super(intake2017);
		requires(intake2017);
		this.intake2017 = intake2017;
		System.out.println("IntakeUp constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeUp init");
	}

	@Override
	protected void execute() {
		intake2017.setPiston(DoubleSolenoid.Value.kReverse);
		intake2017.setIntakeUp(true);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("IntakeUp end");
	}

	@Override
	protected void interrupted() {
		System.out.println("IntakeUp interrupted.");
	}

}
