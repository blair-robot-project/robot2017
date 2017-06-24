package org.usfirst.frc.team449.robot.autonomous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.jetbrains.annotations.NotNull;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.util.YamlCommand;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CenterAuto2017 extends YamlCommandGroupWrapper {

	/**
	 * Default constructor.
	 *
	 * @param runWallToPegProfile The command for running the profile for going from the wall to the peg, which has already been loaded.
	 * @param dropGear The command for dropping the held gear.
	 * @param dropGearSwitch      The switch deciding whether or not to drop the gear.
	 * @param driveBack The command for backing up away from the peg.
	 */
	@JsonCreator
	public CenterAuto2017(
			@JsonProperty(required = true) @NotNull RunLoadedProfile runWallToPegProfile,
			@JsonProperty(required = true) @NotNull YamlCommand dropGear,
			@JsonProperty(required = true) @NotNull MappedDigitalInput dropGearSwitch,
			@JsonProperty(required = true) @NotNull YamlCommand driveBack) {
		addSequential(runWallToPegProfile);
		if (dropGearSwitch.getStatus().get(0)) {
			addSequential(dropGear.getCommand());
		}
		addSequential(driveBack.getCommand());
	}
}
