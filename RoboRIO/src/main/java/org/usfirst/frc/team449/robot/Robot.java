package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import maps.org.usfirst.frc.team449.robot.util.MotionProfileMap;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ShiftingUnidirectionalNavXArcadeDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.UnidirectionalNavXArcadeDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ExecuteProfile;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.ShiftingDrive;
import org.usfirst.frc.team449.robot.interfaces.drive.shifting.commands.SwitchToGear;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands.DriveAtSpeed;
import org.usfirst.frc.team449.robot.interfaces.drive.unidirectional.commands.PIDTest;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands.SpinUpShooter;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidForward;
import org.usfirst.frc.team449.robot.interfaces.subsystem.solenoid.commands.SolenoidReverse;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.FireShooter;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;
import org.usfirst.frc.team449.robot.util.*;
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

	public static final String RESOURCES_PATH = "/home/lvuser/449_resources/";
	private static final double MP_UPDATE_RATE = 0.005;
	public static Robot instance;
	private static long currentTimeMillis;
	private double WHEEL_DIAMETER;
	/**
	 * The shooter subsystem (flywheel only)
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
	 * The pneumatics (maybe doesn't work?)
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
	public ActiveGearSubsystem gearSubsystem;
	private BooleanWrapper commandFinished;
	/**
	 * The object constructed directly from map.cfg.
	 */
	private Robot2017Map.Robot2017 cfg;
	private Notifier MPNotifier;
	private Notifier loggerNotifier;
	private int completedCommands = 0;
	private long startedGearPush = 0;
	private long timeToPushGear;
	private Map<String, MotionProfileData> rightProfiles, leftProfiles;
	private List<RotPerSecCANTalonSRX> talons;
	private boolean redAlliance, dropGear;
	private String allianceString;
	private String position;
	private I2C robotInfo;
	private ShiftingDrive.gear startingGear;
	private int minPointsInBtmMPBuffer;
	private Logger logger;
	private AutoSide autoSide;
	private static long startTime;

	/**
	 * The method that runs when the robot is turned on. Initializes all subsystems from the map.
	 */
	public void robotInit() {
		currentTimeMillis = System.currentTimeMillis();
		startTime = currentTimeMillis;
		System.out.println("Started robotInit.");

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

		List<Loggable> loggables = new ArrayList<>();

		startingGear = cfg.getStartLowGear() ? ShiftingDrive.gear.LOW : ShiftingDrive.gear.HIGH;
		minPointsInBtmMPBuffer = cfg.getMinPointsInBottomMPBuffer();

		if (cfg.hasRioduinoPort()) {
			robotInfo = new I2C(I2C.Port.kOnboard, cfg.getRioduinoPort());
		}

		//Construct the OI (has to be done first because other subsystems take the OI as an argument.)
		oiSubsystem = new OI2017ArcadeGamepad(cfg.getArcadeOi());
		Logger.addEvent("Constructed OI", this.getClass());

		//Construct the drive (not in a if block because you kind of need it.)
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

		//Activate the compressor if its module number is in the map.
		if (cfg.hasModule()) {
			Logger.addEvent("Setting up a compressor at module number " + cfg.getModule(), this.getClass());
			Compressor compressor = new Compressor(cfg.getModule());
			compressor.setClosedLoopControl(true);
			compressor.start();
			Logger.addEvent("Compressor enabled: "+compressor.enabled(), this.getClass());
		}

		//Construct intake if it's in the map.
		if (cfg.hasIntake()) {
			intakeSubsystem = new Intake2017(cfg.getIntake());
		}

		if (cfg.hasGear()) {
			gearSubsystem = new ActiveGearSubsystem(cfg.getGear());
		}

		try {
			logger = new Logger(cfg.getLogger(), loggables);
		} catch (IOException e) {
			System.out.println("Instantiating logger failed!");
			e.printStackTrace();
		}
		loggerNotifier = new Notifier(logger);

		//Map the buttons (has to be done last because all the subsystems need to have been instantiated.)
		oiSubsystem.mapButtons();
		Logger.addEvent("Mapped buttons", this.getClass());

		if (cfg.hasBlueRed()) {
			redAlliance = new MappedDigitalInput(cfg.getBlueRed()).getStatus().get(0);
			if (redAlliance) {
				allianceString = "red";
			} else {
				allianceString = "blue";
			}
			dropGear = new MappedDigitalInput(cfg.getDropGear()).getStatus().get(0);
			List<Boolean> tmp = new MappedDigitalInput(cfg.getLocation()).getStatus();
			if (!tmp.get(0) && !tmp.get(1)) {
				position = "center";
			} else if (tmp.get(0)) {
				position = "left";
			} else {
				position = "right";
			}
		}

		if (position.equals("center")){
			autoSide = AutoSide.CENTER;
		} else if ((position.equals("right") && redAlliance) || (position.equals("left") && !redAlliance)){
			autoSide = AutoSide.BOILER;
		} else {
			autoSide = AutoSide.LOADING_STATION;
		}

		Logger.addEvent("redAlliance: " + redAlliance, this.getClass());
		Logger.addEvent("dropGear: " + dropGear, this.getClass());
		Logger.addEvent("positon: " + position, this.getClass());
		Logger.addEvent("Auto_plan: " + autoSide, this.getClass());

		if (cfg.getDoMP()) {
			WHEEL_DIAMETER = cfg.getWheelDiameterInches() / 12.;
			timeToPushGear = (long) (cfg.getTimeToPushGear() * 1000);

			leftProfiles = new HashMap<>();
			rightProfiles = new HashMap<>();

			for (MotionProfileMap.MotionProfile profile : cfg.getLeftMotionProfileList()) {
				leftProfiles.put(profile.getName(), new MotionProfileData(profile));
			}

			for (MotionProfileMap.MotionProfile profile : cfg.getRightMotionProfileList()) {
				rightProfiles.put(profile.getName(), new MotionProfileData(profile));
			}

			if (cfg.getTestMP()) {
				MPLoader.loadTopLevel(leftProfiles.get("test"), driveSubsystem.leftMaster, WHEEL_DIAMETER);
				MPLoader.loadTopLevel(rightProfiles.get("test"), driveSubsystem.rightMaster, WHEEL_DIAMETER);
			} else {
				MPLoader.loadTopLevel(leftProfiles.get(allianceString + "_" + position), driveSubsystem.leftMaster, WHEEL_DIAMETER);
				MPLoader.loadTopLevel(rightProfiles.get(allianceString + "_" + position), driveSubsystem.rightMaster, WHEEL_DIAMETER);
			}

			talons = new ArrayList<>();
			talons.add(driveSubsystem.leftMaster);
			talons.add(driveSubsystem.rightMaster);
			MPNotifier = MPLoader.startLoadBottomLevel(talons, MP_UPDATE_RATE);
			commandFinished = new BooleanWrapper(false);
			completedCommands = 0;
		}
		logger.run();
	}

	/**
	 * Run when we first enable in teleop.
	 */
	@Override
	public void teleopInit() {
		loggerNotifier.startPeriodic(cfg.getLogger().getLoopTimeSecs());
		currentTimeMillis = System.currentTimeMillis();
		//Stop the drive for safety reasons
		if (MPNotifier != null) {
			MPNotifier.stop();
		}
		driveSubsystem.setVBusThrottle(0, 0);
		driveSubsystem.setOverrideNavX(!cfg.getArcadeOi().getOverrideNavXWhileHeld());

		driveSubsystem.leftMaster.canTalon.enable();
		driveSubsystem.rightMaster.canTalon.enable();

		driveSubsystem.setDefaultCommandManual(new ShiftingUnidirectionalNavXArcadeDrive(driveSubsystem.straightPID, driveSubsystem, oiSubsystem));

//		Scheduler.getInstance().add(new PIDTest(driveSubsystem));
		//Switch to low gear if we have gears
		if (driveSubsystem.shifter != null) {
			Scheduler.getInstance().add(new SwitchToGear(driveSubsystem, startingGear));
		}

		if (intakeSubsystem != null) {
			Scheduler.getInstance().add(new SolenoidReverse(intakeSubsystem));
		}

		if (gearSubsystem != null) {
			Scheduler.getInstance().add(new SolenoidForward(gearSubsystem));
		}

		sendModeOverI2C(robotInfo, "teleop");
	}

	/**
	 * Run every tick in teleop.
	 */
	@Override
	public void teleopPeriodic() {
		currentTimeMillis = System.currentTimeMillis();
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}

	/**
	 * Run when we first enable in autonomous
	 */
	@Override
	public void autonomousInit() {
		loggerNotifier.startPeriodic(cfg.getLogger().getLoopTimeSecs());
		currentTimeMillis = System.currentTimeMillis();
		sendModeOverI2C(robotInfo, "auto");

		//Switch to low gear if we have gears
		if (driveSubsystem.shifter != null) {
			Scheduler.getInstance().add(new SwitchToGear(driveSubsystem, startingGear));
		}

		if (gearSubsystem != null) {
			Scheduler.getInstance().add(new SolenoidForward(gearSubsystem));
		}

		commandFinished.set(false);

		driveSubsystem.setVBusThrottle(0, 0);

		if (cfg.getDoMP()) {
			if (singleFlywheelShooterSubsystem != null && autoSide == AutoSide.BOILER) {
				Scheduler.getInstance().add(new SpinUpShooter(singleFlywheelShooterSubsystem));
			}
			Scheduler.getInstance().add(new ExecuteProfile(talons, 15, minPointsInBtmMPBuffer, commandFinished, driveSubsystem));

		} else {
			Scheduler.getInstance().add(new PIDTest(driveSubsystem, cfg.getDriveBackTime()));
		}
	}

	/**
	 * Runs every tick in autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		currentTimeMillis = System.currentTimeMillis();
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
		if (cfg.getDoMP() && !cfg.getTestMP()) {
			if (Robot.currentTimeMillis() - startedGearPush > timeToPushGear && completedCommands == 1) {
				commandFinished.set(true);
			}
			if (commandFinished.get()) {
				Logger.addEvent("A command finished!", this.getClass());
				completedCommands++;
				commandFinished.set(false);
				if (completedCommands == 1) {
					if (gearSubsystem != null && dropGear) {
						Scheduler.getInstance().add(new SolenoidReverse(gearSubsystem));
					}
					startedGearPush = Robot.currentTimeMillis();
				} else if (completedCommands == 2) {
					if (autoSide == AutoSide.CENTER) {
						Scheduler.getInstance().add(new DriveAtSpeed(driveSubsystem, -0.3, cfg.getDriveBackTime()));
					} else if (position.equals("right") && redAlliance) {
						loadProfile("red_shoot");
						Scheduler.getInstance().add(new ExecuteProfile(talons, 10, minPointsInBtmMPBuffer, commandFinished, driveSubsystem));
					} else if (position.equals("left") && !redAlliance) {
						loadProfile("blue_shoot");
						Scheduler.getInstance().add(new ExecuteProfile(talons, 10, minPointsInBtmMPBuffer, commandFinished, driveSubsystem));
					} else if (redAlliance) {
						loadProfile("red_backup");
						Scheduler.getInstance().add(new ExecuteProfile(talons, 10, minPointsInBtmMPBuffer, commandFinished, driveSubsystem));
					} else {
						loadProfile("blue_backup");
						Scheduler.getInstance().add(new ExecuteProfile(talons, 10, minPointsInBtmMPBuffer, commandFinished, driveSubsystem));
					}
				} else if (completedCommands == 3) {
					if (autoSide == AutoSide.BOILER && singleFlywheelShooterSubsystem != null) {
						Scheduler.getInstance().add(new FireShooter(singleFlywheelShooterSubsystem, intakeSubsystem));
					} else if (autoSide == AutoSide.LOADING_STATION) {
						loadProfile("forward");
						Scheduler.getInstance().add(new ExecuteProfile(talons, 10, minPointsInBtmMPBuffer, commandFinished, driveSubsystem));
					}
				}
			}
		}
	}

	@Override
	public void disabledInit() {
		driveSubsystem.setVBusThrottle(0, 0);
		sendModeOverI2C(robotInfo, "disabled");
	}

	private void loadProfile(String name) {
		MPNotifier.stop();
		MPLoader.loadTopLevel(leftProfiles.get(name), driveSubsystem.leftMaster, WHEEL_DIAMETER);
		MPLoader.loadTopLevel(rightProfiles.get(name), driveSubsystem.rightMaster, WHEEL_DIAMETER);
		MPNotifier.startPeriodic(MP_UPDATE_RATE);
	}

	private void sendModeOverI2C(I2C i2C, String mode) {
		if (i2C != null) {
			char[] CharArray = (allianceString + "_" + mode).toCharArray();
			byte[] WriteData = new byte[CharArray.length];
			for (int i = 0; i < CharArray.length; i++) {
				WriteData[i] = (byte) CharArray[i];
			}
			i2C.transaction(WriteData, WriteData.length, null, 0);
		}
	}

	private enum AutoSide{
		CENTER,BOILER,LOADING_STATION
	}

	public static long currentTimeMillis() {
		return currentTimeMillis - startTime;
	}
}
