package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

/**
 * Created by blairrobot on 2/4/17.
 */
public class ToggleIntakeUpDown extends ReferencingCommandGroup {

	private Intake2017 intake2017;

	public ToggleIntakeUpDown(MappedSubsystem subsystem){
		super(subsystem);
		requires(subsystem);
		intake2017 = (Intake2017) subsystem;

		if (intake2017.intakeUp){
			addSequential(new IntakeDown(intake2017));
		} else {
			addSequential(new IntakeUp(intake2017));
		}
	}
}