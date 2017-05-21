package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.util.BooleanWrapper;

/**
 * Created by noah on 5/20/17.
 */
public class RunProfile extends CommandGroup {

	private CANTalonMPSubsystem subsystem;

	private String profile;

	private double timeout;

	private BooleanWrapper finishFlag;

	public RunProfile(CANTalonMPSubsystem subsystem, String profile, double timeout, BooleanWrapper finishFlag) {
		this.subsystem = subsystem;
		this.profile = profile;
		this.timeout = timeout;
		this.finishFlag = finishFlag;
	}

	@Override
	public void execute() {
		addSequential(new LoadProfile(subsystem, profile));
		addSequential(new RunLoadedProfile(subsystem, timeout, finishFlag, true));
	}
}
