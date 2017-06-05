package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import org.usfirst.frc.team449.robot.drive.talonCluster.ShiftingTalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ShiftingUnidirectionalNavXArcadeDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.UnidirectionalNavXArcadeDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands.SwitchToGear;
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
import org.usfirst.frc.team449.robot.util.Logger;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * The main class of the robot, constructs all the subsystems and initializes default commands.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Robot extends IterativeRobot {

	/**
	 * The absolute filepath to the resources folder containing the config files.
	 */
	private static final String RESOURCES_PATH = "/home/lvuser/449_resources/";

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
	private SingleFlywheelShooter singleFlywheelShooterSubsystem;

	/**
	 * The intake subsystem (intake motors and pistons)
	 */
	private Intake2017 intakeSubsystem;

	/**
	 * The climber
	 */
	private ClimberSubsystem climberSubsystem;

	/**
	 * The compressor and pressure sensor
	 */
	private PneumaticsSubsystem pneumaticsSubsystem;

	/**
	 * The drive
	 */
	private TalonClusterDrive driveSubsystem;

	/**
	 * OI, using an Xbox-style controller and arcade drive.
	 */
	private OI2017ArcadeGamepad oiSubsystem;

	/**
	 * The cameras on the robot and the code to stream them to SmartDashboard (NOT computer vision!)
	 */
	private CameraSubsystem cameraSubsystem;

	/**
	 * The active gear subsystem.
	 */
	private ActiveGearSubsystem gearSubsystem;

	/**
	 * The object constructed directly from map.cfg.
	 */
	private RobotMap cfg;

	/**
	 * The Notifier running the logging thread.
	 */
	private Notifier loggerNotifier;

	/**
	 * The string version of the alliance we're on ("red" or "blue"). Used for string concatenation to pick which
	 * profile to execute.
	 */
	private String allianceString;

	/**
	 * The I2C channel for communicating with the RIOduino.
	 */
	private I2C robotInfo;

	/**
	 * The logger for the robot.
	 */
	private Logger logger;

	/**
	 * The command to run during autonomous.
	 */
	private Command autonomousCommand;

	/**
	 * Get the current time, in milliseconds, since startup.
	 *
	 * @return current time in milliseconds.
	 */
	public static long currentTimeMillis() {
		return currentTimeMillis - startTime;
	}

	/**
	 * The method that runs when the robot is turned on. Initializes all subsystems from the map.
	 */
	public void robotInit() {
		//Set up start time
		currentTimeMillis = System.currentTimeMillis();
		startTime = currentTimeMillis;
		//Yes this should be a print statement, it's useful to know that robotInit started.
		System.out.println("Started robotInit.");

		try {
			YAMLMapper mapper = new YAMLMapper();
			mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			//Try to construct map from the cfg file
			cfg = mapper.readValue(new File(RESOURCES_PATH+"ballbasaur_map.yml"), RobotMap.class);
//			cfg = mapper.readValue(RESOURCES_PATH + "calcifer_map.yml", RobotMap.class);
		} catch (IOException e) {
			//This is either the map file not being in the file system OR it being improperly formatted.
			System.out.println("Config file is bad/nonexistent!");
			e.printStackTrace();
		}

		this.oiSubsystem = cfg.getOi();
		this.logger = cfg.getLogger();
		this.loggerNotifier = new Notifier(logger);
		this.climberSubsystem = cfg.getClimber();
		this.singleFlywheelShooterSubsystem = cfg.getShooter();
		this.cameraSubsystem = cfg.getCamera();
		this.intakeSubsystem = cfg.getIntake();
		this.pneumaticsSubsystem = cfg.getPneumatics();
		this.gearSubsystem = cfg.getGearHandler();

		if (cfg.getShiftingDrive() != null) {
			driveSubsystem = cfg.getShiftingDrive();
		} else {
			driveSubsystem = cfg.getNonShiftingDrive();
		}

		//Set up RIOduino I2C channel if it's in the map.
		if (cfg.getRIOduinoPort() != null) {
			robotInfo = new I2C(I2C.Port.kOnboard, cfg.getRIOduinoPort());
		}

		//Set up the motion profiles if we're doing motion profiling
		if (cfg.getDoMP()) {
			//Load the test profiles if we just want to run one.
			if (cfg.getTestMP()) {
				driveSubsystem.loadMotionProfile(cfg.getLeftTestProfile(), cfg.getRightTestProfile());
				autonomousCommand = new RunLoadedProfile(driveSubsystem, 15, true);
			} else {
				//Read the data from the input switches
				boolean redAlliance = cfg.getAllianceSwitch().getStatus().get(0);
				boolean dropGear = cfg.getDropGearSwitch().getStatus().get(0);
				List<Boolean> tmp = cfg.getLocationDial().getStatus();

				String position;
				//Interpret the pin input from the three-way side selection switch.
				if (!tmp.get(0) && !tmp.get(1)) {
					position = "center";
				} else if (tmp.get(0)) {
					position = "left";
				} else {
					position = "right";
				}

				//Set up the alliance strings for easily selecting profiles.
				if (redAlliance) {
					allianceString = "red";
				} else {
					allianceString = "blue";
				}

				//Log the data read from the switches
				Logger.addEvent("redAlliance: " + redAlliance, this.getClass());
				Logger.addEvent("dropGear: " + dropGear, this.getClass());
				Logger.addEvent("position: " + position, this.getClass());

				//Load the first profile we want to run
				driveSubsystem.loadMotionProfile(cfg.getLeftProfiles().get(allianceString + "_" + position),
						cfg.getRightProfiles().get(allianceString + "_" + position));
				//Set the autonomousCommand to be the correct command for the current position and alliance.
				if (position.equals("center")) {
					autonomousCommand = cfg.getCenterAuto();
				} else if ((position.equals("right") && redAlliance) || (position.equals("left") && !redAlliance)) {
					autonomousCommand = cfg.getBoilerAuto();
				} else {
					autonomousCommand = cfg.getFeederAuto();
				}
			}
		} else {
			autonomousCommand = cfg.getNonMPAutoCommand();
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

		//Enable the motors in case they got disabled somehow
		driveSubsystem.enableMotors();

		//Set the default command
		if (driveSubsystem.getClass().equals(ShiftingTalonClusterDrive.class)) {
			driveSubsystem.setDefaultCommandManual(new ShiftingUnidirectionalNavXArcadeDrive(driveSubsystem.straightPID, ((ShiftingTalonClusterDrive) driveSubsystem), oiSubsystem));
		} else {
			driveSubsystem.setDefaultCommandManual(new UnidirectionalNavXArcadeDrive(driveSubsystem.straightPID, driveSubsystem, oiSubsystem));
		}

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
	 * Sends the current mode (auto, teleop, or disabled) over I2C.
	 *
	 * @param i2C  The I2C channel to send the data over.
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
	private void doStartupTasks() {
		//Start running the logger
		loggerNotifier.startPeriodic(cfg.getLogger().getLoopTimeSecs());
		//Refresh the current time.
		currentTimeMillis = System.currentTimeMillis();
		//Switch to starting gear
		if (driveSubsystem.getClass().equals(ShiftingTalonClusterDrive.class)) {
			Scheduler.getInstance().add(new SwitchToGear((ShiftingTalonClusterDrive) driveSubsystem, ((ShiftingTalonClusterDrive) driveSubsystem).getStartingGear()));
		}

		//Start the compressor if it exists
		if (pneumaticsSubsystem != null) {
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
