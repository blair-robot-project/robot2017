package org.usfirst.frc.team449.robot.autonomous;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.UnidirectionalDrive;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.TwoSideMPSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.TwoSideMPSubsystem.commands.RunProfileTwoSides;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.SpinUpShooter;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.TurnAllOn;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.util.MotionProfileData;

/**
 * The autonomous routine to deliver a gear to the center gear.
 */
public class BoilerAuto2017 extends CommandGroup{

	/**
	 * Default constructor.
	 * @param drive The drive subsystem to execute this command on. Must also be a {@link UnidirectionalDrive}, and
	 *              needs to have the profile to drive up to the peg already loaded into it.
	 * @param gearHandler The gear handler to execute this command on.
	 * @param dropGear Whether or not to drop the gear.
	 * @param leftPegToKeyProfile The motion profile for the left side of the drive to execute to get from the peg to the key.
	 * @param rightPegToKeyProfile The motion profile for the right side of the drive to execute to get from the peg to the key.
	 * @param shooter The shooter subsystem to execute this command on.
	 */
	public BoilerAuto2017(TwoSideMPSubsystem drive, ActiveGearSubsystem gearHandler, boolean dropGear,
	                      MotionProfileData leftPegToKeyProfile, MotionProfileData rightPegToKeyProfile,
	                      ShooterSubsystem shooter){
		addParallel(new SpinUpShooter(shooter));
		addSequential(new RunLoadedProfile(drive, 15, true));
		if (dropGear) {
			addSequential(new SolenoidReverse(gearHandler));
		}
		addSequential(new RunProfileTwoSides(drive, leftPegToKeyProfile, rightPegToKeyProfile, 10));
		addSequential(new TurnAllOn(shooter));
	}
}
