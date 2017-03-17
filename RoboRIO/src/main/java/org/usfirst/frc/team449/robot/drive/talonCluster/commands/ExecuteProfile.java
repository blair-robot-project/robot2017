package org.usfirst.frc.team449.robot.drive.talonCluster.commands;

import com.ctre.CANTalon;
import edu.wpi.first.wpilibj.Notifier;
import maps.org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDriveMap;
import org.usfirst.frc.team449.robot.ReferencingCommand;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MPUpdaterProcess;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MotionProfileData;

/**
 * ReferencingCommand to load and execute a motion profile on the master Talons in the two motor clusters
 */
public class ExecuteProfile extends ReferencingCommand {
	private static final double WHEEL_DIAMETER = 4; //In inches
	//TODO Externalize all this shit
	/**
	 * Number of points that must be loaded to the bottom level buffer before we start executing the profile
	 */
	private static final int MIN_NUM_POINTS_IN_BTM = 128; // maximum number of points

	/**
	 * Filenames of the MP input files
	 */
	private static final String LEFT_IN_FILE_NAME = "/home/lvuser/449_resources/leftProfile.csv";
	private static final String RIGHT_IN_FILE_NAME = "/home/lvuser/449_resources/rightProfile.csv";

	/**
	 * Update rate of the {@link MPUpdaterProcess}
	 */
	private static final double UPDATE_RATE = 0.005;    // MP processing thread update rate copied from CTRE example

	// TODO make _state and enum
	/**
	 * State variable of the FSM used to chose when to load points and when to execute the profile
	 */
	private int _state = 0;

	/**
	 * Scheduler {@link Notifier} for updating the Talons' MP
	 */
	private Notifier mpProcessNotifier;

	/**
	 * Drive that the MP will be executed on
	 */
	private TalonClusterDrive tcd;

	/**
	 * Motion profile data
	 *
	 * @deprecated to be replaced with PathGenerator stuff in MP_2_Sides future
	 */
	private MotionProfileData leftProfile;
	private MotionProfileData rightProfile;

	/**
	 * MP status of the left master
	 */
	private CANTalon.MotionProfileStatus leftStatus;

	/**
	 * MP status of the right master
	 */
	private CANTalon.MotionProfileStatus rightStatus;

	private boolean finished;

	/**
	 * Construct a new ExecuteProfile command
	 *
	 * @param subsystem drive subsystem to execute this command on
	 */
	public ExecuteProfile(TalonClusterDrive subsystem) {
		super(subsystem);
		requires(subsystem);

		tcd = subsystem;

		leftStatus = new CANTalon.MotionProfileStatus();
		rightStatus = new CANTalon.MotionProfileStatus();

		leftProfile = new MotionProfileData(LEFT_IN_FILE_NAME);
		rightProfile = new MotionProfileData(RIGHT_IN_FILE_NAME);

		finished = false;

		mpProcessNotifier = null;   // WARNING not assigned until after "initialize" is called
	}

	/**
	 * Set up the Talons' modes and populate the trajectory point buffer
	 */
	@Override
	protected void initialize() {
		// Put the masters in motion profile mode
		tcd.leftMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
		tcd.rightMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);

		// Make sure they are disabled while they have data piped into them
		tcd.leftMaster.canTalon.set(CANTalon.SetValueMotionProfile.Disable.value);
		tcd.rightMaster.canTalon.set(CANTalon.SetValueMotionProfile.Disable.value);

		tcd.leftMaster.canTalon.clearMotionProfileHasUnderrun();
		tcd.rightMaster.canTalon.clearMotionProfileHasUnderrun();

		finished = false;
	}

	/**
	 * If its the first execute call, start the thread. Otherwise, error check every loop call. Note that the real
	 * logic is executed in the control method for black-magic Scheduler timing reasons.
	 */
	@Override
	protected void execute() {
		control();
		/*
		SmartDashboard.putNumber("Left MP Error", tcd.leftMaster.getSpeed() - tcd.leftMaster.nativeToRPS(leftStatus.activePoint.velocity));
		SmartDashboard.putNumber("Right MP Error", tcd.rightMaster.getSpeed() - tcd.rightMaster.nativeToRPS(rightStatus.activePoint.velocity));
		System.out.println("Active Point: " + pointToString(leftStatus.activePoint));
		System.out.println("Output Enable: " + leftStatus.outputEnable + ", " + rightStatus.outputEnable);
		if (!leftStatus.activePointValid) {
			System.out.println("INVALID! YOU DONE FUCKED UP LEFT SIDE");
			System.out.println("Left active point: " + pointToString(leftStatus.activePoint));
		}
		if (!rightStatus.activePointValid) {
			System.out.println("INVALID! YOU DONE FUCKED UP RIGHT SIDE");
			System.out.println("Right active point: " + pointToString(rightStatus.activePoint));
		}
		if (leftStatus.activePoint.isLastPoint || rightStatus.activePoint.isLastPoint) {
			System.out.println("LAST POINT");
		}
		if (leftStatus.activePoint.zeroPos || rightStatus.activePoint.zeroPos) {
			System.out.println("FIRST POINT");
		}
		*/
		tcd.logData();
	}

	@Override
	protected boolean isFinished(){
		return finished;
	}

	@Override
	protected void end(){
		tcd.leftMaster.canTalon.set(CANTalon.SetValueMotionProfile.Hold.value);
		tcd.rightMaster.canTalon.set(CANTalon.SetValueMotionProfile.Hold.value);
		mpProcessNotifier.stop();
		System.out.println("ExecuteProfile end.");
	}

	@Override
	protected void interrupted(){
		mpProcessNotifier.stop();
		tcd.leftMaster.canTalon.set(CANTalon.SetValueMotionProfile.Disable.value);
		tcd.rightMaster.canTalon.set(CANTalon.SetValueMotionProfile.Disable.value);
		System.out.println("ExecuteProfile interrupted!");
	}

	/**
	 * Execute the logic that controls if trajectory points are being loaded or if the profile is being executed
	 */
	private void control() {
		tcd.leftMaster.canTalon.getMotionProfileStatus(leftStatus);
		tcd.rightMaster.canTalon.getMotionProfileStatus(rightStatus);

		if (leftStatus.isUnderrun || rightStatus.isUnderrun)
			System.out.println("UNDERRUN! That's BAAAD!");

		switch (_state) {
			case 0:
//				System.out.println("State 0.");
				startFilling();
				_state = 1;
				break;
			case 1:
//				System.out.println("State 1");
				mpProcessNotifier.startPeriodic(UPDATE_RATE);
				tcd.leftMaster.canTalon.changeMotionControlFramePeriod((int) (UPDATE_RATE * 1e3));  // TODO figure out what this does
				tcd.rightMaster.canTalon.changeMotionControlFramePeriod((int) (UPDATE_RATE * 1e3));
				System.out.println("LEFT BTM BUFF CNT " + leftStatus.btmBufferCnt);
				System.out.println("RIGHT BTM BUFF CNT " + rightStatus.btmBufferCnt);

				if (leftStatus.btmBufferCnt >= MIN_NUM_POINTS_IN_BTM && rightStatus.btmBufferCnt >= MIN_NUM_POINTS_IN_BTM) {
					_state = 2;
					System.out.println("LOADED");
				} else {
					System.out.println("NOT FULLY LOADED");
				}
				break;
			case 2:
//				System.out.println("State 2");
				tcd.leftMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
				tcd.rightMaster.canTalon.changeControlMode(CANTalon.TalonControlMode.MotionProfile);
				tcd.leftMaster.canTalon.enable();
				tcd.rightMaster.canTalon.enable();
				tcd.leftMaster.canTalon.set(CANTalon.SetValueMotionProfile.Enable.value);
				tcd.rightMaster.canTalon.set(CANTalon.SetValueMotionProfile.Enable.value);
				tcd.leftMaster.canTalon.getMotionProfileStatus(leftStatus);
				tcd.rightMaster.canTalon.getMotionProfileStatus(rightStatus);
				if(leftStatus.outputEnable  == CANTalon.SetValueMotionProfile.Enable && rightStatus.outputEnable  == CANTalon.SetValueMotionProfile.Enable)
					_state = 3;
				break;
			case 3:
//				System.out.println("State 3");
				if(leftStatus.activePoint.isLastPoint && rightStatus.activePoint.isLastPoint){
					finished = true;
				}
				break;
			default:
				System.out.println("Default state, something went wrong.");
				break;
		}
	}

	/**
	 * Fill the Talon MP hardware buffer
	 */
	private void startFilling() {
		MPUpdaterProcess updaterProcess = new MPUpdaterProcess();

		tcd.rightMaster.canTalon.clearMotionProfileTrajectories();
		tcd.leftMaster.canTalon.clearMotionProfileTrajectories();

		// Fill the Talon's buffer with points
		CANTalon.TrajectoryPoint point = new CANTalon.TrajectoryPoint();
		for (int i = 0; i < leftProfile.data.length; ++i) {
			// Set all the fields of the profile point
			point.position = -inchesToNative(leftProfile.data[i][0]);
			point.velocity = -tcd.leftMaster.RPStoNative(leftProfile.data[i][1]);
			point.timeDurMs = (int) (leftProfile.data[i][2] * 1000.);
			point.profileSlotSelect = 1;    // gain selection
			point.velocityOnly = false;  // true => no position servo just velocity feedforward
			point.zeroPos = i == 0; // If its the first point, zeroPos  =  true
			point.isLastPoint = (i + 1) == leftProfile.data.length; // If its the last point, isLastPoint = true

			// Send the point to the Talon's buffer
			if(!tcd.leftMaster.canTalon.pushMotionProfileTrajectory(point)) {
				System.out.println("Left buffer full!");
				break;
			}

//			System.out.println("LEFT POINT "+(i+1)+": "+pointToString(point));
		}

		for (int i = 0; i < rightProfile.data.length; ++i) {
			// Set all the fields of the profile point
			point.position = -inchesToNative(rightProfile.data[i][0]) * ((TalonClusterDriveMap.TalonClusterDrive) tcd.map).getL2R();
			point.velocity = -tcd.leftMaster.RPStoNative(rightProfile.data[i][1]) * ((TalonClusterDriveMap.TalonClusterDrive) tcd.map).getL2R();
			point.timeDurMs = (int) (rightProfile.data[i][2] * 1000.);
			point.profileSlotSelect = 1;    // gain selection
			point.velocityOnly = false;  // true => no position servo just velocity feedforward
			point.zeroPos = i == 0; // If its the first point, zeroPos  =  true
			point.isLastPoint = (i + 1) == rightProfile.data.length; // If its the last point, isLastPoint = true

			// Send the point to the Talon's buffer
			if (!tcd.rightMaster.canTalon.pushMotionProfileTrajectory(point)){
				System.out.println("Right buffer full!");
				break;
			}

//			System.out.println("RIGHT POINT "+(i+1)+": "+pointToString(point));
		}


		// Add the Talons to the updater thread (thread should not have started yet tho)
		updaterProcess.addTalon(tcd.leftMaster.canTalon);
		updaterProcess.addTalon(tcd.rightMaster.canTalon);
		mpProcessNotifier = new Notifier(updaterProcess);
		System.out.println("Finished loading points");
	}

	private double nativeToInches(double nativeUnits){
		double rotations = nativeUnits / (tcd.leftMaster.encoderCPR*4);
		return rotations * (WHEEL_DIAMETER*Math.PI);
	}

	private double inchesToNative(double inches){
		double rotations = inches / (WHEEL_DIAMETER*Math.PI);
		return rotations * (tcd.leftMaster.encoderCPR*4);
	}

	private String pointToString(CANTalon.TrajectoryPoint point){
		return "(pos = " + nativeToInches(point.position) + " in, vel = " + tcd.leftMaster.nativeToRPS(point.velocity) + " rps, dT = " + point.timeDurMs+" ms)";
	}
}
