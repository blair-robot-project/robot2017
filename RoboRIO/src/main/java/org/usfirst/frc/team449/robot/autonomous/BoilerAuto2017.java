package org.usfirst.frc.team449.robot.autonomous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.commands.RunProfileTwoSides;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.SpinUpShooter;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.TurnAllOn;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.util.MotionProfileData;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class BoilerAuto2017 <T extends YamlSubsystem & TwoSideMPSubsystem> extends YamlCommandGroupWrapper {

	/**
	 * Default constructor.
	 *
	 * @param drive                The drive subsystem to execute this command on. Must have the profile to drive up to
	 *                             the peg already loaded into it.
	 * @param gearHandler          The gear handler to execute this command on.
	 * @param dropGear             The switch deciding whether or not to drop the gear.
	 * @param allianceSwitch The switch indicating which alliance we're on.
	 * @param blueLeftPegToKeyProfile  The motion profile for the left side of the drive to execute to get from the peg to
	 *                             the key on the blue alliance.
	 * @param blueRightPegToKeyProfile The motion profile for the right side of the drive to execute to get from the peg to
	 *                             the key on the blue alliance.
	 * @param redLeftPegToKeyProfile  The motion profile for the left side of the drive to execute to get from the peg to
	 *                             the key on the red alliance.
	 * @param redRightPegToKeyProfile The motion profile for the right side of the drive to execute to get from the peg to
	 *                             the key on the red alliance.
	 * @param shooter              The shooter subsystem to execute this command on. Can be null.
	 */
	@JsonCreator
	public BoilerAuto2017(@JsonProperty(required = true) T drive,
	                                                                     @JsonProperty(required = true) ActiveGearSubsystem gearHandler,
	                                                                     @JsonProperty(required = true) MappedDigitalInput dropGear,
	                                                                     @JsonProperty(required = true) MappedDigitalInput allianceSwitch,
	                                                                     @JsonProperty(required = true) MotionProfileData blueLeftPegToKeyProfile,
	                                                                     @JsonProperty(required = true) MotionProfileData blueRightPegToKeyProfile,
	                                                                     @JsonProperty(required = true) MotionProfileData redLeftPegToKeyProfile,
	                                                                     @JsonProperty(required = true) MotionProfileData redRightPegToKeyProfile,
	                                                                     ShooterSubsystem shooter) {
		if (shooter != null) {
			addParallel(new SpinUpShooter(shooter));
		}
		addSequential(new RunLoadedProfile(drive, 15, true));
		if (dropGear.getStatus().get(0)) {
			addSequential(new SolenoidReverse(gearHandler));
		}
		if (allianceSwitch.getStatus().get(0)) {
			addSequential(new RunProfileTwoSides(drive, redLeftPegToKeyProfile, redRightPegToKeyProfile, 10));
		} else {
			addSequential(new RunProfileTwoSides(drive, blueLeftPegToKeyProfile, blueRightPegToKeyProfile, 10));
		}
		addSequential(new TurnAllOn(shooter));
	}
}
