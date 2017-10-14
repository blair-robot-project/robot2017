package org.usfirst.frc.team449.robot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.usfirst.frc.team449.robot.drive.unidirectional.DriveTalonCluster;
import org.usfirst.frc.team449.robot.other.Clock;
import org.usfirst.frc.team449.robot.other.Logger;
import org.usfirst.frc.team449.robot.subsystem.interfaces.motionProfile.commands.RunLoadedProfile;
import org.yaml.snakeyaml.Yaml;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The main class of the robot, constructs all the subsystems and initializes default commands.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Robot extends IterativeRobot {

	/**
	 * The absolute filepath to the resources folder containing the config files.
	 */
	@NotNull
	public static final String RESOURCES_PATH = "/home/lvuser/449_resources/";

	/**
	 * The drive
	 */
	private DriveTalonCluster driveSubsystem;

	/**
	 * The object constructed directly from the yaml map.
	 */
	private RobotMap2017 robotMap;

	/**
	 * The Notifier running the logging thread.
	 */
	private Notifier loggerNotifier;

	/**
	 * The string version of the alliance we're on ("red" or "blue"). Used for string concatenation to pick which
	 * profile to execute.
	 */
	@Nullable
	private String allianceString;

	/**
	 * The I2C channel for communicating with the RIOduino.
	 */
	@Nullable
	private I2C robotInfo;

	/**
	 * The command to run during autonomous. Null to do nothing during autonomous.
	 */
	@Nullable
	private Command autonomousCommand;

	/**
	 * The method that runs when the robot is turned on. Initializes all subsystems from the map.
	 */
	public void robotInit() {
		//Set up start time
		Clock.setStartTime();
		Clock.updateTime();

		//Yes this should be a print statement, it's useful to know that robotInit started.
		System.out.println("Started robotInit.");

		Yaml yaml = new Yaml();
		try {
			//Read the yaml file with SnakeYaml so we can use anchors and merge syntax.
			Map<?, ?> normalized = (Map<?, ?>) yaml.load(new FileReader(RESOURCES_PATH+"ballbasaur_map.yml"));
//			Map<?, ?> normalized = (Map<?, ?>) yaml.load(new FileReader(RESOURCES_PATH + "calcifer_map.yml"));
//			Map<?, ?> normalized = (Map<?, ?>) yaml.load(new FileReader(RESOURCES_PATH + "calcifer_outreach_map.yml"));
			YAMLMapper mapper = new YAMLMapper();
			//Turn the Map read by SnakeYaml into a String so Jackson can read it.
			String fixed = mapper.writeValueAsString(normalized);
			//Use a parameter name module so we don't have to specify name for every field.
			mapper.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
			//Deserialize the map into an object.
			robotMap = mapper.readValue(fixed, RobotMap2017.class);
		} catch (IOException e) {
			//This is either the map file not being in the file system OR it being improperly formatted.
			System.out.println("Config file is bad/nonexistent!");
			e.printStackTrace();
		}
		//Set fields from the map.
		this.loggerNotifier = new Notifier(robotMap.getLogger());
		this.driveSubsystem = robotMap.getDrive();

		//Set up RIOduino I2C channel if it's in the map.
		if (robotMap.getRIOduinoPort() != null) {
			robotInfo = new I2C(I2C.Port.kOnboard, robotMap.getRIOduinoPort());
		}

		//Set up the motion profiles if we're doing motion profiling
		if (robotMap.getDoMP()) {
			//Load the test profiles if we're testing.
			if (robotMap.getTestMP()) {
				driveSubsystem.loadMotionProfile(robotMap.getLeftTestProfile(), robotMap.getRightTestProfile());
				autonomousCommand = new RunLoadedProfile<>(driveSubsystem, 15, true);
			} else {
				//Read the data from the input switches
				boolean redAlliance = robotMap.getAllianceSwitch().getStatus().get(0);
				boolean dropGear = robotMap.getDropGearSwitch().getStatus().get(0);
				List<Boolean> tmp = robotMap.getLocationDial().getStatus();

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

				SmartDashboard.putString("Position", allianceString + " " + position);
				SmartDashboard.putBoolean("DropGear", dropGear);

				//Load the first profile we want to run
				driveSubsystem.loadMotionProfile(robotMap.getLeftProfiles().get(allianceString + "_" + position),
						robotMap.getRightProfiles().get(allianceString + "_" + position));
				//Set the autonomousCommand to be the correct command for the current position and alliance.
				if (position.equals("center")) {
					autonomousCommand = robotMap.getCenterAuto();
				} else if ((position.equals("right") && redAlliance) || (position.equals("left") && !redAlliance)) {
					autonomousCommand = robotMap.getBoilerAuto();
				} else {
					autonomousCommand = robotMap.getFeederAuto();
				}
			}
		} else {
			autonomousCommand = robotMap.getNonMPAutoCommand();
		}

		//Run the logger to write all the events that happened during initialization to a file.
		robotMap.getLogger().run();
		Clock.updateTime();
	}

	/**
	 * Run when we first enable in teleop.
	 */
	@Override
	public void teleopInit() {
		//Do the startup tasks
		driveSubsystem.stopMPProcesses();
		doStartupTasks();
		if (robotMap.getTeleopStartupCommand() != null) {
			robotMap.getTeleopStartupCommand().start();
		}

		//Set the default command
		driveSubsystem.setDefaultCommandManual(robotMap.getDefaultDriveCommand());

		//Tell the RIOduino that we're in teleop
		sendModeOverI2C(robotInfo, "teleop");
	}

	/**
	 * Run every tick in teleop.
	 */
	@Override
	public void teleopPeriodic() {
		//Refresh the current time.
		Clock.updateTime();
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}

	/**
	 * Run when we first enable in autonomous
	 */
	@Override
	public void autonomousInit() {
		//Do startup tasks
		doStartupTasks();
		if (robotMap.getAutoStartupCommand() != null) {
			robotMap.getAutoStartupCommand().start();
		}

		//Start running the autonomous command
		if (autonomousCommand != null) {
			autonomousCommand.start();
		}

		//Tell the RIOduino we're in autonomous
		sendModeOverI2C(robotInfo, "auto");
	}

	/**
	 * Runs every tick in autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Update the current time
		Clock.updateTime();
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
	 * Run every tic while disabled
	 */
	@Override
	public void disabledPeriodic(){
		Clock.updateTime();
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
		//Refresh the current time.
		Clock.updateTime();

		//Start running the logger
		loggerNotifier.startPeriodic(robotMap.getLogger().getLoopTimeSecs());
	}
}
