package org.usfirst.frc.team449.robot.autonomous;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands.DriveAtSpeed;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.util.BooleanWrapper;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
public class CenterAuto2017 extends CommandGroup{

	/**
	 * Default constructor.
	 * @param drive The drive subsystem to execute this command on. Must also be a {@link UnidirectionalDrive}, and
	 *              needs to have the profile to drive up to the peg already loaded into it.
	 * @param gearHandler The gear handler to execute this command on.
	 * @param dropGear Whether or not to drop the gear.
	 * @param driveBackTime How long, in seconds, to drive back from the peg for.
	 */
	public CenterAuto2017(TwoSideMPSubsystem drive, ActiveGearSubsystem gearHandler, boolean dropGear, double driveBackTime){
		addSequential(new RunLoadedProfile(drive, 15, true));
		if (dropGear) {
			addSequential(new SolenoidReverse(gearHandler));
		}
		addSequential(new DriveAtSpeed((UnidirectionalDrive) drive, -0.3, driveBackTime));
	}
}
