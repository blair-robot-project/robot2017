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
 * ReferencingCommand to load and execute a motion profile on the master Talons in the two motor clusters
 */
public class RunLoadedProfile extends Command {

	private boolean bottomLoaded;

	private List<CANTalon> talons;

	private boolean finished;

	private long timeout;

	private long startTime;

	private BooleanWrapper finishFlag;

	private CANTalonMPSubsystem subsystem;


	/**
	 * Construct a new RunLoadedProfile command
	 */
	public RunLoadedProfile(CANTalonMPSubsystem subsystem, double timeout, BooleanWrapper finishFlag, boolean require) {
		this.subsystem = subsystem;
		if (require) {
			requires((Subsystem) subsystem);
		}

		this.timeout = (long) (timeout * 1000);

		talons = subsystem.getTalons();
		finished = false;
		bottomLoaded = false;
		this.finishFlag = finishFlag;
	}

	/**
	 * Set up the Talons' modes and populate the trajectory point buffer
	 */
	@Override
	protected void initialize() {
		for (CANTalon talon : talons) {
			talon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
			talon.set(CANTalon.SetValueMotionProfile.Disable.value);
			talon.clearMotionProfileHasUnderrun();
		}

		startTime = Robot.currentTimeMillis();

		finished = false;
		bottomLoaded = false;
	}

	@Override
	protected void execute() {
		finished = true;
		boolean bottomNowLoaded = true;
		for (CANTalon talon : talons) {
			CANTalon.MotionProfileStatus MPStatus = new CANTalon.MotionProfileStatus();
			talon.getMotionProfileStatus(MPStatus);
			if (!bottomLoaded) {
				finished = false;
				bottomNowLoaded = bottomNowLoaded && (MPStatus.btmBufferCnt >= subsystem.getMinPointsInBtmBuffer() || MPStatus.topBufferCnt == 0);
			}
			if (bottomLoaded) {
				finished = finished && MPStatus.activePoint.isLastPoint;
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
		finishFlag.set(true);
		Logger.addEvent("RunLoadedProfile end.", this.getClass());
	}

	@Override
	protected void interrupted() {
		for (CANTalon talon : talons) {
			talon.set(CANTalon.SetValueMotionProfile.Disable.value);
		}
		finishFlag.set(true);
		Logger.addEvent("RunLoadedProfile interrupted!", this.getClass());
	}
}
