package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake;

/**
 * Created by Justin on 1/28/2017.
 */
public class IntakeUp extends ReferencingCommand {

	Intake intake;

	public IntakeUp(Intake intake) {
		super(intake);
		requires(intake);
		this.intake = intake;
		System.out.println("IntakeUp constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeUp init");
	}

	@Override
	protected void execute() {
		intake.setPiston(DoubleSolenoid.Value.kReverse);
	}

	@Override
	protected boolean isFinished() {
		return false;
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
