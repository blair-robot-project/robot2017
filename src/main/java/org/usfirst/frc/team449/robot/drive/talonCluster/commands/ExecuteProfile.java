package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Notifier;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MPUpdaterProcess;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MotionProfileData;

/**
 * Load and execute a motion profile on the master Talons in the two motor clusters
 */
public class ExecuteProfile extends ReferencingCommand {
	private static final int MIN_NUM_LOADED_POINTS = 5; // copied off of the CTRE example code
	private static final String IN_FILE_NAME = "profile.csv";
	private static final double UPDATE_RATE = 0.005;    // MP processing thread update rate copied from CTRE example

	private static boolean started = false;

	private Notifier mpProcessNotifier;

	private TalonClusterDrive tcd;
	private MotionProfileData profile;

	private CANTalon.MotionProfileStatus leftStatus;
	private CANTalon.MotionProfileStatus rightStatus;

	public ExecuteProfile(TalonClusterDrive subsystem) {
		super(subsystem);
		tcd = subsystem;

		leftStatus = new CANTalon.MotionProfileStatus();
		rightStatus = new CANTalon.MotionProfileStatus();

		profile = new MotionProfileData(IN_FILE_NAME);
		mpProcessNotifier = null;   // WARNING not assigned until after "initialize" is called
	}

	/**
	 * Set up the Talons' modes and populate the trajectory point buffer
	 */
	@Override
	protected void initialize() {
		MPUpdaterProcess updaterProcess = new MPUpdaterProcess();

		// Put the masters in motion profile mode
		tcd.leftMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
		tcd.rightMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);

		// Make sure they are disabled while they have data piped into them
		tcd.leftMaster.canTalon.set(CANTalon.SetValueMotionProfile.Disable.value);
		tcd.rightMaster.canTalon.set(CANTalon.SetValueMotionProfile.Disable.value);

		// Fill the Talon's buffer with points
		CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();
		for (int i = 0; i < profile.data.length; ++i) {
			// Set all the fields of the profile point
			point.position = profile.data[i][0];
			point.velocity = profile.data[i][1];
			point.timeDurMs = (int) profile.data[i][2];
			point.profileSlotSelect = 0;    // gain selection
			point.velocityOnly = true;  // true => no position servo just velocity feedforward
			point.zeroPos = false;
			point.zeroPos = i == 0; // If its the first point, zeroPos  =  true
			point.isLastPoint = (i + 1) == profile.data.length; // If its the last point, isLastPoint = true

			// Send the point to the Talon's buffer
			tcd.leftMaster.canTalon.pushMotionProfileTrajectory(point);
			tcd.rightMaster.canTalon.pushMotionProfileTrajectory(point);
		}

		// Add the Talons to the updater thread (thread should not have started yet tho)
		updaterProcess.addTalon(tcd.leftMaster.canTalon);
		updaterProcess.addTalon(tcd.rightMaster.canTalon);
		mpProcessNotifier = new Notifier(updaterProcess);
	}

	/**
	 * If its the first execute call, start the thread
	 * Other than that, error check every loop call
	 */
	@Override
	protected void execute() {
		// If the updater has not been started yet, start it
		// TODO see if you can put this in the initializer method
		if (!started) {
			mpProcessNotifier.startPeriodic(UPDATE_RATE);
			started = true;
		}

		// Bunch of error detection shit
		tcd.leftMaster.canTalon.getMotionProfileStatus(leftStatus);
		tcd.rightMaster.canTalon.getMotionProfileStatus(rightStatus);
		if (leftStatus.isUnderrun || rightStatus.isUnderrun) {
			System.out.println("IS UNDERRAN");
			System.exit(-1);
		} else if (leftStatus.hasUnderrun || rightStatus.hasUnderrun) {
			System.out.println("HAS UNDERRUN");
			System.exit(-1);
		} else if (!(leftStatus.activePointValid && rightStatus.activePointValid)) {
			System.out.println("ACTIVE POINT INVALID");
			System.exit(-1);
		} else if ((leftStatus.btmBufferCnt < MIN_NUM_LOADED_POINTS) || (rightStatus.btmBufferCnt <
				MIN_NUM_LOADED_POINTS)) {
			System.out.println("HAS NOT LOADED ENOUGH POINTS");
			System.exit(-1);
		}
	}
}
