package org.usfirst.frc.team449.robot.autonomous;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.usfirst.frc.team449.robot.util.YamlCommandGroupWrapper;
import org.usfirst.frc.team449.robot.util.YamlSubsystem;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands.DriveAtSpeed;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class CenterAuto2017 extends YamlCommandGroupWrapper {

	/**
	 * Default constructor.
	 *
	 * @param drive         The drive subsystem to execute this command on. Must have the profile to drive up to the
	 *                      peg
	 *                      already loaded into it.
	 * @param gearHandler   The gear handler to execute this command on.
	 * @param dropGear      Whether or not to drop the gear.
	 * @param driveBackTime How long, in seconds, to drive back from the peg for.
	 */
	@JsonCreator
	public <T extends YamlSubsystem & UnidirectionalDrive & TwoSideMPSubsystem> CenterAuto2017(
			@JsonProperty(required = true) T drive,
			@JsonProperty(required = true) ActiveGearSubsystem gearHandler,
			@JsonProperty(required = true) boolean dropGear,
			@JsonProperty(required = true) double driveBackTime) {
		addSequential(new RunLoadedProfile(drive, 15, true));
		if (dropGear) {
			addSequential(new SolenoidReverse(gearHandler));
		}
		addSequential(new DriveAtSpeed(drive, -0.3, driveBackTime));
	}
}
