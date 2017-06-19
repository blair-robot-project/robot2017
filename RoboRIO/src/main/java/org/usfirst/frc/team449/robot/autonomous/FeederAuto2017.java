package org.usfirst.frc.team449.robot.autonomous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.commands.RunProfileTwoSides;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunProfile;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class FeederAuto2017 extends YamlCommandGroupWrapper {

	/**
	 * Default constructor.
	 *
	 * @param drive              The drive subsystem to execute this command on. Must have the profile to drive up to
	 *                           the peg already loaded into it.
	 * @param gearHandler        The gear handler to execute this command on.
	 * @param dropGear           The switch deciding whether or not to drop the gear.
	 * @param allianceSwitch The switch indicating which alliance we're on.
	 * @param redLeftBackupProfile  The motion profile for the left side of the drive to execute to back up from the peg on the red alliance.
	 * @param redRightBackupProfile The motion profile for the right side of the drive to execute to back up from the peg on the red alliance.
	 * @param blueLeftBackupProfile  The motion profile for the left side of the drive to execute to back up from the peg on the blue alliance.
	 * @param blueRightBackupProfile The motion profile for the right side of the drive to execute to back up from the peg on the blue alliance.
	 * @param forwardsProfile    The motion profile for both sides to drive forwards after backing up from the peg.
	 */
	@JsonCreator
	public <T extends YamlSubsystem & TwoSideMPSubsystem> FeederAuto2017(@JsonProperty(required = true) T drive,
	                                                                 @JsonProperty(required = true) ActiveGearSubsystem gearHandler,
	                                                                 @JsonProperty(required = true) MappedDigitalInput dropGear,
	                                                                 @JsonProperty(required = true) MappedDigitalInput allianceSwitch,
	                                                                 @JsonProperty(required = true) MotionProfileData redLeftBackupProfile,
	                                                                 @JsonProperty(required = true) MotionProfileData redRightBackupProfile,
                                                                     @JsonProperty(required = true) MotionProfileData blueLeftBackupProfile,
                                                                     @JsonProperty(required = true) MotionProfileData blueRightBackupProfile,
	                                                                 @JsonProperty(required = true) MotionProfileData forwardsProfile) {
		addSequential(new RunLoadedProfile(drive, 15, true));
		if (dropGear.getStatus().get(0)) {
			addSequential(new SolenoidReverse(gearHandler));
		}
		if (allianceSwitch.getStatus().get(0)) {
			addSequential(new RunProfileTwoSides(drive, redLeftBackupProfile, redRightBackupProfile, 10));
		} else {
			addSequential(new RunProfileTwoSides(drive, blueLeftBackupProfile, blueRightBackupProfile, 10));
		}
		addSequential(new RunProfile(drive, forwardsProfile, 5));
	}
}
