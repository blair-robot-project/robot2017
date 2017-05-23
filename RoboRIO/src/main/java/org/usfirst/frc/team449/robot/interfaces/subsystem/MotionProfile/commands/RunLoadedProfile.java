package org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.CANTalonMPSubsystem;
import org.usfirst.frc.team449.robot.util.BooleanWrapper;
import org.usfirst.frc.team449.robot.util.Logger;

import java.util.List;

/**
 * Runs the command that is currently loaded in the given subsystem.
 */
public class RunLoadedProfile extends Command {

	/**
	 * Whether the bottom-level MP buffer is sufficiently loaded to begin moving the robot.
	 */
	private boolean bottomLoaded;

	/**
	 * The talons to execute this profile on.
	 */
	private List<CANTalon> talons;

	/**
	 * An internal flag to signal if the command is finished yet.
	 */
	private boolean finished;

	/**
	 * The amount of time this command is allowed to run for, in milliseconds.
	 */
	private long timeout;

	/**
	 * The time this command started running at.
	 */
	private long startTime;

	/**
	 * The subsystem to execute this command on.
	 */
	private CANTalonMPSubsystem subsystem;


	/**
	 * Default constructor.
	 *
	 * @param subsystem The subsystem to execute this command on.
	 * @param timeout The max amount of time this subsystem is allowed to run for, in seconds.
	 * @param require Whether or not to require the subsystem this command is running on.
	 */
	public RunLoadedProfile(CANTalonMPSubsystem subsystem, double timeout, boolean require) {
		this.subsystem = subsystem;
		//Require if specified.
		if (require) {
			requires((Subsystem) subsystem);
		}

		//Convert to milliseconds.
		this.timeout = (long) (timeout * 1000);

		talons = subsystem.getTalons();
		finished = false;
		bottomLoaded = false;
	}

	/**
	 * Set up the Talons' modes and the start time.
	 */
	@Override
	protected void initialize() {
		//Reset the talons
		for (CANTalon talon : talons) {
			talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
			talon.set(CANTalon.SetValueMotionProfile.Disable.value);
			talon.clearMotionProfileHasUnderrun();
		}

		//Record the start time.
		startTime = Robot.currentTimeMillis();

		//Set finished flags to false.
		finished = false;
		bottomLoaded = false;
	}

	/**
	 * If the bottom buffer is loaded, start running the profile. While running the profile, check if it's finished.
	 */
	@Override
	protected void execute() {
		//We set these to true here, but then loop through each talon and set them to false if they're false for any Talon.
		finished = true;
		boolean bottomNowLoaded = true;

		for (CANTalon talon : talons) {
			//Get the status of the profile being run
			CANTalon.MotionProfileStatus MPStatus = new CANTalon.MotionProfileStatus();
			talon.getMotionProfileStatus(MPStatus);

			//If the bottom is loaded (i.e. we're running the profile)
			if (bottomLoaded) {
				//Keep finished as true if the last point is the one active, and make it false otherwise.
				finished = finished && MPStatus.activePoint.isLastPoint;
			}
			//If we're still waiting for the bottom buffer to fill up
			else {
				//Then of course the profile isn't done
				finished = false;
				//Keep bottomNowLoaded as true if the bottom buffer is sufficiently full or there aren't any points left in the top buffer.
				bottomNowLoaded = bottomNowLoaded && (MPStatus.btmBufferCnt >= subsystem.getMinPointsInBtmBuffer() || MPStatus.topBufferCnt == 0);
			}
		}

		if (bottomNowLoaded && !bottomLoaded) {
			bottomLoaded = true;
			Logger.addEvent("Enabling the talons!", this.getClass());
			for (CANTalon talon : talons) {
				talon.enable();
				talon.set(CANTalon.SetValueMotionProfile.Enable.value);
			}
		}
	}

	@Override
	protected boolean isFinished() {
		return finished || (Robot.currentTimeMillis() - startTime > timeout);
	}

	@Override
	protected void end() {
		for (CANTalon talon : talons) {
			talon.set(CANTalon.SetValueMotionProfile.Hold.value);
		}
		Logger.addEvent("RunLoadedProfile end.", this.getClass());
	}

	@Override
	protected void interrupted() {
		for (CANTalon talon : talons) {
			talon.set(CANTalon.SetValueMotionProfile.Disable.value);
		}
		Logger.addEvent("RunLoadedProfile interrupted!", this.getClass());
	}
}
