package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Command;

public class FakeIntakeUp extends Command {

	protected void execute() {
		DoubleSolenoid piston = new DoubleSolenoid(4, 5);
		piston.set(DoubleSolenoid.Value.kForward);
	}
	@Override
	protected boolean isFinished() {
		return true;
	}
}
