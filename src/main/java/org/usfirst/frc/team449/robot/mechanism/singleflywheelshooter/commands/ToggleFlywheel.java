package org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.ReferencingCommandGroup;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;

/**
 * Created by blairrobot on 1/10/17.
 */
@Deprecated
public class ToggleFlywheel extends ReferencingCommandGroup {

	private SingleFlywheelShooter shooterSubsystem;

	public ToggleFlywheel(MappedSubsystem subsystem) {
		super(subsystem);
		requires(subsystem);
		shooterSubsystem = (SingleFlywheelShooter) subsystem;

		if (shooterSubsystem.spinning) {
			addSequential(new DecelerateFlywheel(shooterSubsystem, 1));
		} else {
			addSequential(new AccelerateFlywheel(shooterSubsystem, 1));
		}
	}
}
