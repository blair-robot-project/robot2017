package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import maps.org.usfirst.frc.team449.robot.util.MotionProfileMap;
import org.usfirst.frc.team449.robot.autonomous.BoilerAuto2017;
import org.usfirst.frc.team449.robot.autonomous.CenterAuto2017;
import org.usfirst.frc.team449.robot.autonomous.FeederAuto2017;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ShiftingUnidirectionalNavXArcadeDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands.SwitchToGear;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands.PIDTest;
import org.usfirst.frc.team449.robot.interfaces.subsystem.MotionProfile.commands.RunLoadedProfile;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidForward;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.commands.StartCompressor;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;
import org.usfirst.frc.team449.robot.util.Loggable;
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.util.MotionProfileData;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main class of the robot, constructs all the subsystems and initializes default commands.
 */
public class Robot extends IterativeRobot {

	/**
	 * The absolute filepath to the resources folder containing the config files.
	 */
	public static final String RESOURCES_PATH = "/home/lvuser/449_resources/";

	/**
	 * The instance of this object that exists. This is a static field so that the subsystems don't have to be.
	 */
	public static Robot instance;

	/**
	 * The current time in milliseconds as it was stored the last time a method in robot was run.
	 */
	private static long currentTimeMillis;

	/**
	 * The time robotInit started running.
	 */
	private static long startTime;

	/**
	 * The shooter subsystem (flywheel and feeder)
	 */
	public SingleFlywheelShooter singleFlywheelShooterSubsystem;

	/**
	 * The intake subsystem (intake motors and pistons)
	 */
	public Intake2017 intakeSubsystem;

	/**
	 * The climber
	 */
	public ClimberSubsystem climberSubsystem;

	/**
	 * The compressor and pressure sensor
	 */
	public PneumaticsSubsystem pneumaticsSubsystem;

	/**
	 * The drive
	 */
	public TalonClusterDrive driveSubsystem;

	/**
	 * OI, using an Xbox-style controller and arcade drive.
	 */
	public OI2017ArcadeGamepad oiSubsystem;

	/**
	 * The cameras on the robot and the code to stream them to SmartDashboard (NOT computer vision!)
	 */
	public CameraSubsystem cameraSubsystem;

	/**
	 * The active gear subsystem.
	 */
	public ActiveGearSubsystem gearSubsystem;

	/**
	 * The object constructed directly from map.cfg.
	 */
	private Robot2017Map.Robot2017 cfg;

	/**
	 * The Notifier running the logging thread.
	 */
	private Notifier loggerNotifier;

	/**
	 * Whether or not we're on the red alliance, according to the dIO switch.
	 */
	private boolean redAlliance;

	/**
	 * Whether or not to drop the gear during autonomous, according to the dIO switch.
	 */
	private boolean dropGear;

	/**
	 * The position we're in, according to the dIO switch.
	 */
	private String position;

	/**
	 * The string version of the alliance we're on ("red" or "blue"). Used for string concatenation to pick which profile to execute.
	 */
	private String allianceString;

	/**
	 * The I2C channel for communicating with the RIOduino.
	 */
	private I2C robotInfo;

	/**
	 * The gear to start both autonomous and teleop in.
	 */
	private ShiftingDrive.gear startingGear;

	/**
	 * The logger for the robot.
	 */
	private Logger logger;

	/**
	 * The command to run during autonomous.
	 */
	private Command autonomousCommand;

	/**
	 * The method that runs when the robot is turned on. Initializes all subsystems from the map.
	 */
	public void robotInit() {
		//Set up start time
		currentTimeMillis = System.currentTimeMillis();
		startTime = currentTimeMillis;
		//Yes this should be a print statement, it's useful to know that robotInit started.
		System.out.println("Started robotInit.");

		//Set up instance.
		instance = this;

		try {
			//Try to construct map from the cfg file
//			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig(RESOURCES_PATH+"balbasaur_map.cfg",
			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig(RESOURCES_PATH + "fancy_map.cfg",
					Robot2017Map.Robot2017.newBuilder());
		} catch (IOException e) {
			//This is either the map file not being in the file system OR it being improperly formatted.
			System.out.println("Config file is bad/nonexistent!");
			e.printStackTrace();
		}

		//Instantiate the list of loggable subsystems.
		List<Loggable> loggables = new ArrayList<>();

		//Set up starting gear from the map.
		startingGear = cfg.getStartLowGear() ? ShiftingDrive.gear.LOW : ShiftingDrive.gear.HIGH;

		//Construct the OI (has to be done first because other subsystems take the OI as an argument.)
		oiSubsystem = new OI2017ArcadeGamepad(cfg.getArcadeOi());
		Logger.addEvent("Constructed OI", this.getClass());

		//Construct the drive (not in an "if null" block because you kind of need it.)
		driveSubsystem = new TalonClusterDrive(cfg.getDrive(), oiSubsystem, startingGear);
		Logger.addEvent("Constructed Drive", this.getClass());
		loggables.add(driveSubsystem);

		//Construct camera if it's in the map.
		if (cfg.hasCamera()) {
			cameraSubsystem = new CameraSubsystem(cfg.getCamera());
		}

		//Construct climber if it's in the map.
		if (cfg.hasClimber()) {
			climberSubsystem = new ClimberSubsystem(cfg.getClimber());
			loggables.add(climberSubsystem);
		}

		//Construct shooter if it's in the map.
		if (cfg.hasShooter()) {
			singleFlywheelShooterSubsystem = new SingleFlywheelShooter(cfg.getShooter());
			loggables.add(singleFlywheelShooterSubsystem);
			Logger.addEvent("Constructed SingleFlywheelShooter", this.getClass());
		}

		//Construct pneumatics if it's in the map.
		if (cfg.hasPneumatics()) {
			pneumaticsSubsystem = new PneumaticsSubsystem(cfg.getPneumatics());
			loggables.add(pneumaticsSubsystem);
			Logger.addEvent("Constructed PneumaticsSubsystem", this.getClass());
		}

		//Construct intake if it's in the map.
		if (cfg.hasIntake()) {
			intakeSubsystem = new Intake2017(cfg.getIntake());
		}

		//Construct active gear if it's in the map.
		if (cfg.hasGear()) {
			gearSubsystem = new ActiveGearSubsystem(cfg.getGear());
		}

		//Map the buttons (has to be done last because all the subsystems need to have been instantiated.)
		oiSubsystem.mapButtons();
		Logger.addEvent("Mapped buttons", this.getClass());

		//Try to instantiate the logger.
		try {
			logger = new Logger(cfg.getLogger(), loggables);
			//Set up the Notifier that runs the logging thread.
			loggerNotifier = new Notifier(logger);
		} catch (IOException e) {
			System.out.println("Instantiating logger failed!");
			e.printStackTrace();
		}

		//Set up RIOduino I2C channel if it's in the map.
		if (cfg.hasRioduinoPort()) {
			robotInfo = new I2C(I2C.Port.kOnboard, cfg.getRioduinoPort());
		}

		//Read the dIO pins if they're in the map
		if (cfg.hasBlueRed()) {
			//Read from the pins
			redAlliance = new MappedDigitalInput(cfg.getBlueRed()).getStatus().get(0);
			dropGear = new MappedDigitalInput(cfg.getDropGear()).getStatus().get(0);
			List<Boolean> tmp = new MappedDigitalInput(cfg.getLocation()).getStatus();

			//Interpret the pin input from the three-way side selection switch.
			if (!tmp.get(0) && !tmp.get(1)) {
				position = "center";
			} else if (tmp.get(0)) {
				position = "left";
			} else {
				position = "right";
			}

			//Set up allianceString to use for concatenation.
			if (redAlliance) {
				allianceString = "red";
			} else {
				allianceString = "blue";
			}
		}

		//Log the data read from the switches
		Logger.addEvent("redAlliance: " + redAlliance, this.getClass());
		Logger.addEvent("dropGear: " + dropGear, this.getClass());
		Logger.addEvent("position: " + position, this.getClass());

		//Instantiate the profile maps.
		Map<String, MotionProfileData> rightProfiles = new HashMap<>(), leftProfiles = new HashMap<>();

		//Fill the profile data with the profiles. This part takes a little while because we have to read all the files.
		for (MotionProfileMap.MotionProfile profile : cfg.getLeftMotionProfileList()) {
			leftProfiles.put(profile.getName(), new MotionProfileData(profile));
		}
		for (MotionProfileMap.MotionProfile profile : cfg.getRightMotionProfileList()) {
			rightProfiles.put(profile.getName(), new MotionProfileData(profile));
		}

		//Set up the motion profiles if we're doing motion profiling
		if (cfg.getDoMP()) {
			//Load the test profiles if we just want to run one.
			if (cfg.getTestMP()) {
				driveSubsystem.loadMotionProfile(leftProfiles.get("test"), rightProfiles.get("test"));
				autonomousCommand = new RunLoadedProfile(driveSubsystem, 15, true);
			} else {
				//Load the first profile we want to run
				driveSubsystem.loadMotionProfile(leftProfiles.get(allianceString + "_" + position), rightProfiles.get(allianceString + "_" + position));
				//Set the autonomousCommand to be the correct command for the current position and alliance.
				if (position.equals("center")) {
					autonomousCommand = new CenterAuto2017(driveSubsystem, gearSubsystem, dropGear, cfg.getDriveBackTime());
				} else if ((position.equals("right") && redAlliance) || (position.equals("left") && !redAlliance)) {
					autonomousCommand = new BoilerAuto2017(driveSubsystem, gearSubsystem, dropGear,
							leftProfiles.get(allianceString + "_shoot"), rightProfiles.get(allianceString + "_shoot"),
							singleFlywheelShooterSubsystem);
				} else {
					autonomousCommand = new FeederAuto2017(driveSubsystem, gearSubsystem, dropGear,
							leftProfiles.get(allianceString + "_backup"), rightProfiles.get(allianceString + "_backup"),
							leftProfiles.get("forward"));
				}
			}
		} else {
			autonomousCommand = new PIDTest(driveSubsystem, cfg.getDriveBackTime());
		}
		//Run the logger to write all the events that happened during initialization to a file.
		logger.run();
	}

	/**
	 * Run when we first enable in teleop.
	 */
	@Override
	public void teleopInit() {
		//Stop the drive for safety reasons
		driveSubsystem.stopMPProcesses();
		driveSubsystem.fullStop();
		//Set up whether to override the NavX based on whether it's overriden by default.
		driveSubsystem.setOverrideNavX(!cfg.getArcadeOi().getOverrideNavXWhileHeld());

		//Enable the motors in case they got disabled somehow
		driveSubsystem.enableMotors();

		//Set the default command
		driveSubsystem.setDefaultCommandManual(new ShiftingUnidirectionalNavXArcadeDrive(driveSubsystem.straightPID, driveSubsystem, oiSubsystem));

		//Do the startup tasks
		doStartupTasks();

		//Tell the RIOduino that we're in teleop
		sendModeOverI2C(robotInfo, "teleop");
	}

	/**
	 * Run every tick in teleop.
	 */
	@Override
	public void teleopPeriodic() {
		//Refresh the current time.
		currentTimeMillis = System.currentTimeMillis();
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}

	/**
	 * Run when we first enable in autonomous
	 */
	@Override
	public void autonomousInit() {
		//Stop the drive for safety reasons
		driveSubsystem.fullStop();

		//Do startup tasks
		doStartupTasks();

		//Start running the autonomous command
		autonomousCommand.start();

		//Tell the RIOduino we're in autonomous
		sendModeOverI2C(robotInfo, "auto");
	}

	/**
	 * Runs every tick in autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Update the current time
		currentTimeMillis = System.currentTimeMillis();
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}

	/**
	 * Run when we disable.
	 */
	@Override
	public void disabledInit() {
		//Fully stop the drive
		driveSubsystem.fullStop();
		//Tell the RIOduino we're disabled.
		sendModeOverI2C(robotInfo, "disabled");
	}

	/**
	 * Get the current time, in milliseconds, since startup.
	 * @return current time in milliseconds.
	 */
	public static long currentTimeMillis() {
		return currentTimeMillis - startTime;
	}

	/**
	 * Sends the current mode (auto, teleop, or disabled) over I2C.
	 * @param i2C The I2C channel to send the data over.
	 * @param mode The current mode, represented as a String.
	 */
	private void sendModeOverI2C(I2C i2C, String mode) {
		//If the I2C exists
		if (i2C != null) {
			//Turn the alliance and mode into a character array.
			char[] CharArray = (allianceString + "_" + mode).toCharArray();
			//Transfer the character array to a byte array.
			byte[] WriteData = new byte[CharArray.length];
			for (int i = 0; i < CharArray.length; i++) {
				WriteData[i] = (byte) CharArray[i];
			}
			//Send the byte array.
			i2C.transaction(WriteData, WriteData.length, null, 0);
		}
	}

	/**
	 * Do tasks that should be done when we first enable, in both auto and teleop.
	 */
	private void doStartupTasks(){
		//Start running the logger
		loggerNotifier.startPeriodic(cfg.getLogger().getLoopTimeSecs());
		//Refresh the current time.
		currentTimeMillis = System.currentTimeMillis();
		//Switch to starting gear
		Scheduler.getInstance().add(new SwitchToGear(driveSubsystem, startingGear));

		//Start the compressor if it exists
		if (pneumaticsSubsystem != null){
			Scheduler.getInstance().add(new StartCompressor(pneumaticsSubsystem));
		}

		//Put up the intake if it exists
		if (intakeSubsystem != null) {
			Scheduler.getInstance().add(new SolenoidReverse(intakeSubsystem));
		}

		//Close the gear handler if it exists
		if (gearSubsystem != null) {
			Scheduler.getInstance().add(new SolenoidForward(gearSubsystem));
		}
	}
}
