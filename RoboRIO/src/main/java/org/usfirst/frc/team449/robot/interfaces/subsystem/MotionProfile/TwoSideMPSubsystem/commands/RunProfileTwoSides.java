package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * Loads and runs the given profiles into the given subsystem.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class RunProfileTwoSides <T extends YamlSubsystem & TwoSideMPSubsystem> extends YamlCommandGroupWrapper {

	/**
	 * Default constructor.
	 *
	 * @param subsystem The subsystem to execute this command on.
	 * @param left      The motion profile for the left side to load and execute.
	 * @param right     The motion profile for the right side to load and execute.
	 * @param timeout   The maximum amount of time this command is allowed to take, in seconds.
	 */
	@JsonCreator
	public RunProfileTwoSides(@JsonProperty(required = true) T subsystem,
	                                                                     @JsonProperty(required = true) MotionProfileData left,
	                                                                     @JsonProperty(required = true) MotionProfileData right,
	                                                                     @JsonProperty(required = true) double timeout) {
		addSequential(new LoadProfileTwoSides(subsystem, left, right));
		addSequential(new RunLoadedProfile(subsystem, timeout, true));
	}
}
