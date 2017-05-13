package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.Robot;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.util.BooleanWrapper;

import java.util.ArrayList;
import java.util.Collection;

/**
 * ReferencingCommand to load and execute a motion profile on the master Talons in the two motor clusters
 */
public class ExecuteProfile extends Command {
	/**
	 * Number of points that must be loaded to the bottom level buffer before we start executing the profile
	 */
	private static int MIN_NUM_POINTS_IN_BTM;

	private boolean bottomLoaded;

	private Collection<CANTalon> talons;

	private boolean finished;

	private long timeout;

	private long startTime;

	private BooleanWrapper finishFlag;


	/**
	 * Construct a new ExecuteProfile command
	 */
	public ExecuteProfile(Collection<RotPerSecCANTalonSRX> talons, double timeout, int minPointsInBtm, BooleanWrapper finishFlag, Subsystem toRequire) {
		if (toRequire != null) {
			requires(toRequire);
		}

		this.talons = new ArrayList<>();

		for (RotPerSecCANTalonSRX talon : talons) {
			this.talons.add(talon.canTalon);
		}

		this.timeout = (long) (timeout * 1000);

		finished = false;
		bottomLoaded = false;
		MIN_NUM_POINTS_IN_BTM = minPointsInBtm;
		this.finishFlag = finishFlag;
	}

	public ExecuteProfile(Collection<RotPerSecCANTalonSRX> talons, double timeout, int minPointsinBtm, BooleanWrapper finishFlag) {
		this(talons, timeout, minPointsinBtm, finishFlag, null);
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

		startTime = System.currentTimeMillis();

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
				bottomNowLoaded = bottomNowLoaded && (MPStatus.btmBufferCnt >= MIN_NUM_POINTS_IN_BTM || MPStatus.topBufferCnt == 0);
			}
			if (bottomLoaded) {
				finished = finished && MPStatus.activePoint.isLastPoint;
			}
		}
		if (bottomNowLoaded && !bottomLoaded) {
			bottomLoaded = true;
			System.out.println("Enabling the talons!");
			for (CANTalon talon : talons) {
				talon.enable();
				talon.set(CANTalon.SetValueMotionProfile.Enable.value);
			}
		}
		Robot.instance.driveSubsystem.logData();
	}

	@Override
	protected boolean isFinished() {
		return finished || (System.currentTimeMillis() - startTime > timeout);
	}

	@Override
	protected void end() {
		for (CANTalon talon : talons) {
			talon.set(CANTalon.SetValueMotionProfile.Hold.value);
		}
		finishFlag.set(true);
		System.out.println("ExecuteProfile end.");
	}

	@Override
	protected void interrupted() {
		for (CANTalon talon : talons) {
			talon.set(CANTalon.SetValueMotionProfile.Disable.value);
		}
		finishFlag.set(true);
		System.out.println("ExecuteProfile interrupted!");
	}
}
