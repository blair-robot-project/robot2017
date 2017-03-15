package org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.updown;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;

/**
 * Command for raising the intake
 */
public class IntakeUp extends ReferencingCommand {
	/**
	 * The intake subsystem to execute this command on
	 */
	private Intake2017 intake2017;

	/**
	 * Construct an IntakeDown command
	 *
	 * @param intake2017 intake subsystem to execute this command on
	 */
	public IntakeUp(Intake2017 intake2017) {
		super(intake2017);
		this.intake2017 = intake2017;
		System.out.println("IntakeUp constructed");
	}

	@Override
	protected void initialize(){
		System.out.println("IntakeUp init");
	}

	/**
	 * Set the piston to be in up position
	 */
	@Override
	protected void execute() {
		intake2017.setPiston(DoubleSolenoid.Value.kReverse);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end(){
		System.out.println("IntakeUp end");
	}
}
