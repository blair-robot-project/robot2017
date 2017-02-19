package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.updown;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Created by Justin on 1/28/2017.
 */
public class IntakeDown extends ReferencingCommand {

	Intake2017 intake2017;

	public IntakeDown(Intake2017 intake2017) {
		super(intake2017);
		requires(intake2017);
		this.intake2017 = intake2017;
		System.out.println("IntakeDown constructed");
	}

	@Override
	protected void initialize() {
		System.out.println("IntakeDown init");
	}

	@Override
	protected void execute() {
		intake2017.setPiston(DoubleSolenoid.Value.kForward);
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