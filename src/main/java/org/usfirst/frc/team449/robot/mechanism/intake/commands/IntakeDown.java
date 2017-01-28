package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake;

/**
 * Created by Justin on 1/28/2017.
 */
public class IntakeDown extends ReferencingCommand {

	Intake intake;

	public IntakeDown(Intake intake) {
		super(intake);
		requires(intake);
		this.intake = intake;
		System.out.println("IntakeDown constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeDown init");
	}

	@Override
	protected void execute() {
		intake.setPiston(DoubleSolenoid.Value.kForward);
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		System.out.println("IntakeDown end");
	}

	@Override
	protected void interrupted() {
		System.out.println("IntakeDown interrupted.");
	}

}