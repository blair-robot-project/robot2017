package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.util.BooleanWrapper;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * Created by noah on 5/20/17.
 */
public class RunProfileTwoSides extends CommandGroup {

	private TwoSideMPSubsystem subsystem;

	private MotionProfileData left, right;

	private double timeout;

	private BooleanWrapper finishFlag;

	public RunProfileTwoSides(TwoSideMPSubsystem subsystem, MotionProfileData left, MotionProfileData right, double timeout, BooleanWrapper finishFlag) {
		this.subsystem = subsystem;
		this.left = left;
		this.right = right;
		this.timeout = timeout;
		this.finishFlag = finishFlag;
	}

	@Override
	public void execute() {
		addSequential(new LoadProfileTwoSides(subsystem, left, right));
		addSequential(new RunLoadedProfile(subsystem, timeout, finishFlag, true));
	}
}
