package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.util.BooleanWrapper;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * Loads and runs the given profile into the given subsystem.
 */
public class RunProfile extends CommandGroup {

	public RunProfile(CANTalonMPSubsystem subsystem, MotionProfileData profile, double timeout, BooleanWrapper finishFlag) {
		addSequential(new LoadProfile(subsystem, profile));
		addSequential(new RunLoadedProfile(subsystem, timeout, finishFlag, true));
	}
}
