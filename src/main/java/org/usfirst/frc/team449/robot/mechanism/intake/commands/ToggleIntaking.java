package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;

/**
 * Created by blairrobot on 1/28/17.
 */
public class ToggleIntaking extends ReferencingCommandGroup{

	private Intake2017 intake2017;

	public ToggleIntaking(MappedSubsystem subsystem){
		super(subsystem);
		requires(subsystem);
		intake2017 = (Intake2017) subsystem;

		if (intake2017.isIntaking){
			addSequential(new IntakeStop(intake2017));
		} else {
			addSequential(new IntakeIn(intake2017));
		}
	}
}
