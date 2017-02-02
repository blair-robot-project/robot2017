package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Notifier;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MPUpdaterProcess;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MotionProfileData;

/**
 * Load and execute a motion profile on the master Talons in the two motor clusters
 */
public class ExecuteProfile extends ReferencingCommand {
	private static final int MIN_NUM_LOADED_POINTS = 200; // total number of points
	private static final String IN_FILE_NAME = "/home/lvuser/profile.csv";
	private static final double UPDATE_RATE = 0.005;    // MP processing thread update rate copied from CTRE example
	private int _state = 0;
	private Notifier mpProcessNotifier;

	private TalonClusterDrive tcd;
	private MotionProfileData profile;

	private CANTalon.MotionProfileStatus leftStatus;
	private CANTalon.MotionProfileStatus rightStatus;

	public ExecuteProfile(TalonClusterDrive subsystem) {
		super(subsystem);
		requires(subsystem);

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
	}

	/**
	 * If its the first execute call, start the thread
	 * Other than that, error check every loop call
	 */
	@Override
	protected void execute() {
		control();
		System.out.println("Active Points' Velocities: " + leftStatus.activePoint.velocity + ", " + rightStatus
				.activePoint.velocity);
		System.out.println("Active Points' Positions: " + leftStatus.activePoint.position + ", " + rightStatus
				.activePoint.position);
		System.out.println("Output Enable: " + leftStatus.outputEnable + ", " + rightStatus.outputEnable);
		if (!leftStatus.activePointValid || !rightStatus.activePointValid) {
			System.out.println("INVALID! YOU DONE FUCKED UP");
		}
		if (leftStatus.activePoint.isLastPoint || rightStatus.activePoint.isLastPoint) {
			System.out.println("LAST POINT");
		}
		if (leftStatus.activePoint.zeroPos || rightStatus.activePoint.zeroPos) {
			System.out.println("FIRST POINT");
		}
		tcd.logData();
	}

	private void control() {
		tcd.leftMaster.canTalon.getMotionProfileStatus(leftStatus);
		tcd.rightMaster.canTalon.getMotionProfileStatus(rightStatus);
		// TODO take this out
		tcd.leftTPointStatus = leftStatus;
		tcd.rightTPointStatus = rightStatus;

		switch (_state) {
			case 0: {
				startFilling();
				_state = 1;
				break;
			}
			case 1: {
				mpProcessNotifier.startPeriodic(UPDATE_RATE);
				tcd.leftMaster.canTalon.changeMotionControlFramePeriod((int) (UPDATE_RATE * 1e3));  // TODO figure out
				// what this does
				tcd.rightMaster.canTalon.changeMotionControlFramePeriod((int) (UPDATE_RATE * 1e3));
				System.out.println("LEFT BTM BUFF CNT " + leftStatus.btmBufferCnt);
				System.out.println("RIGHT BTM BUFF CNT " + rightStatus.btmBufferCnt);

				if (leftStatus.btmBufferCnt >= 128 || rightStatus.btmBufferCnt >= 128) {
					_state = 2;
					System.out.println("LOADED");
				} else {
					System.out.println("NOT FULLY LOADED");
				}
				break;
			}
			case 2: {
				if (leftStatus.btmBufferCnt < MIN_NUM_LOADED_POINTS || rightStatus.btmBufferCnt <
						MIN_NUM_LOADED_POINTS) {
					tcd.leftMaster.canTalon.setEncPosition(0);
					tcd.rightMaster.canTalon.setEncPosition(0);

					tcd.leftMaster.canTalon.set(CANTalon.SetValueMotionProfile.Enable.value);
					tcd.rightMaster.canTalon.set(CANTalon.SetValueMotionProfile.Enable.value);
					System.out.println("CAN buffer loaded; clearing underrun");
					tcd.leftMaster.canTalon.clearMotionProfileHasUnderrun();
					tcd.rightMaster.canTalon.clearMotionProfileHasUnderrun();
					_state = 3;
				}
			}
			case 3: {
				// Do nothing
			}
		}
	}

	private void startFilling() {
		MPUpdaterProcess updaterProcess = new MPUpdaterProcess();

		// Put the masters in motion profile mode
		tcd.leftMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
		tcd.rightMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);

		// Make sure they are disabled while they have data piped into them
		tcd.leftMaster.canTalon.set(CANTalon.SetValueMotionProfile.Disable.value);
		tcd.rightMaster.canTalon.set(CANTalon.SetValueMotionProfile.Disable.value);

		// Clear all old motion profile settings
		tcd.leftMaster.canTalon.clearMotionProfileHasUnderrun();
		tcd.leftMaster.canTalon.clearMotionProfileTrajectories();
		tcd.rightMaster.canTalon.clearMotionProfileHasUnderrun();
		tcd.rightMaster.canTalon.clearMotionProfileTrajectories();

		// Fill the Talon's buffer with points
		CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();
		for (int i = 0; i < profile.data.length; ++i) {
			// Set all the fields of the profile point
			point.position = profile.data[i][0] * 2048;
			point.velocity = profile.data[i][1] * 2048;
			point.timeDurMs = (int) profile.data[i][2];
			point.profileSlotSelect = 1;    // gain selection
			point.velocityOnly = false;  // true => no position servo just velocity feedforward
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
		System.out.println("Finished loading points");
	}
}
