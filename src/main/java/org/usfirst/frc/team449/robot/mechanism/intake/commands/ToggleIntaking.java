package org.usfirst.frc.team449.robot.mechanism.intake.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake;

/**
 * Created by blairrobot on 1/28/17.
 */
public class ToggleIntaking extends ReferencingCommandGroup{

	private Intake intake;

	public ToggleIntaking(MappedSubsystem subsystem){
		super(subsystem);
		requires(subsystem);
		intake = (Intake) subsystem;

		if (intake.isIntaking){
			addSequential(new IntakeStop(intake));
		} else {
			addSequential(new IntakeIn(intake));
		}
	}
}
