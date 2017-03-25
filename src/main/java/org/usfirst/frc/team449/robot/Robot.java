package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import maps.org.usfirst.frc.team449.robot.components.MotionProfileMap;
import org.usfirst.frc.team449.robot.components.MappedDigitalInput;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.*;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MPLoader;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MotionProfileData;
import org.usfirst.frc.team449.robot.mechanism.activegear.ActiveGearSubsystem;
import org.usfirst.frc.team449.robot.mechanism.activegear.commands.FirePiston;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.updown.IntakeUp;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.commands.AccelerateFlywheel;
import org.usfirst.frc.team449.robot.mechanism.topcommands.shooter.FireShooter;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;
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

	public static double WHEEL_DIAMETER;

	private static final double MP_UPDATE_RATE = 0.005;

	private Notifier MPNotifier;


	/**
	 * The shooter subsystem (flywheel only)
	 */
	public static SingleFlywheelShooter singleFlywheelShooterSubsystem;

	/**
	 * The intake subsystem (intake motors and pistons)
	 */
	public static Intake2017 intakeSubsystem;

	/**
	 *The climber
	 */
	public static ClimberSubsystem climberSubsystem;

	/**
	 *The pneumatics (maybe doesn't work?)
	 */
	public static PneumaticsSubsystem pneumaticsSubsystem;

	/**
	 *The drive
	 */
	public static TalonClusterDrive driveSubsystem;

	/**
	 * OI, using an Xbox-style controller and arcade drive.
	 */
	public static OI2017ArcadeGamepad oiSubsystem;

	/**
	 * The cameras on the robot and the code to stream them to SmartDashboard (NOT computer vision!)
	 */
	public static CameraSubsystem cameraSubsystem;

	/**
	 * The auger used to feed balls into the shooter.
	 */
	public static FeederSubsystem feederSubsystem;

	public static ActiveGearSubsystem gearSubsystem;

	/**
	 * The object constructed directly from map.cfg.
	 * */
	private static Robot2017Map.Robot2017 cfg;

	public static boolean commandFinished = false;

	private int completedCommands = 0;

	private long startedGearPush = 0;

	private long timeToPushGear;

	private Map<String, MotionProfileData> rightProfiles, leftProfiles;

	private List<RotPerSecCANTalonSRX> talons;

	private boolean redAlliance, dropGear;

	private String position;

	private I2C robotInfo;

	/**
	 * The method that runs when the robot is turned on. Initializes all subsystems from the map.
	 */
	public void robotInit() {
		System.out.println("Started robotInit.");

		try {
			//Try to construct map from the cfg file
//			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/balbasaur_map.cfg",
			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/fancy_map.cfg",
					Robot2017Map.Robot2017.newBuilder());
		} catch (IOException e) {
			//This is either the map file not being in the file system OR it being improperly formatted.
			System.out.println("Config file is bad/nonexistent!");
			e.printStackTrace();
		}

		if (cfg.hasRioduinoPort()) {
			robotInfo = new I2C(I2C.Port.kOnboard, cfg.getRioduinoPort());
		}

		//Construct the OI (has to be done first because other subsystems take the OI as an argument.)
		oiSubsystem = new OI2017ArcadeGamepad(cfg.getArcadeOi());
		System.out.println("Constructed OI");

		//Construct the drive (not in a if block because you kind of need it.)
		driveSubsystem = new TalonClusterDrive(cfg.getDrive(), oiSubsystem);
		System.out.println("Constructed Drive");

		//Construct camera if it's in the map.
		if (cfg.hasCamera()) {
			cameraSubsystem = new CameraSubsystem(cfg.getCamera());
		}

		//Construct climber if it's in the map.
		if (cfg.hasClimber()) {
			climberSubsystem = new ClimberSubsystem(cfg.getClimber());
		}

		//Construct shooter if it's in the map.
		if (cfg.hasShooter()) {
			singleFlywheelShooterSubsystem = new SingleFlywheelShooter(cfg.getShooter());
			System.out.println("Constructed SingleFlywheelShooter");
		}

		//Construct pneumatics if it's in the map.
		if (cfg.hasPneumatics()) {
			pneumaticsSubsystem = new PneumaticsSubsystem(cfg.getPneumatics());
			System.out.println("Constructed PneumaticsSubsystem");
		}

		//Construct intake if it's in the map.
		if (cfg.hasIntake()) {
			intakeSubsystem = new Intake2017(cfg.getIntake());
		}

		//Construct feeder if it's in the map.
		if (cfg.hasFeeder()) {
			feederSubsystem = new FeederSubsystem(cfg.getFeeder());
		}

		if(cfg.hasGear()){
			gearSubsystem = new ActiveGearSubsystem(cfg.getGear());
		}

		if(cfg.hasBlueRed()){
			redAlliance = new MappedDigitalInput(cfg.getBlueRed()).getStatus().get(0);
			dropGear = new MappedDigitalInput(cfg.getDropGear()).getStatus().get(0);
			List<Boolean> tmp = new MappedDigitalInput(cfg.getLocation()).getStatus();
			if (!tmp.get(0) && !tmp.get(1)){
				position = "center";
			} else if (tmp.get(0)){
				position = "left";
			} else {
				position = "right";
			}
		}

		System.out.println("redAlliance: "+redAlliance);
		System.out.println("dropGear: "+dropGear);
		System.out.println("positon: "+position);

		//Map the buttons (has to be done last because all the subsystems need to have been instantiated.)
		oiSubsystem.mapButtons();
		System.out.println("Mapped buttons");

		//Activate the compressor if its module number is in the map.
		if (cfg.hasModule()) {
			System.out.println("Setting up a compressor at module number "+cfg.getModule());
			Compressor compressor = new Compressor(cfg.getModule());
			compressor.setClosedLoopControl(true);
			compressor.start();
			System.out.println(compressor.enabled());
		}

		if(cfg.getDoMP()) {
			WHEEL_DIAMETER = cfg.getWheelDiameterInches() / 12.;
			timeToPushGear = (long) (cfg.getTimeToPushGear() * 1000);

			leftProfiles = new HashMap<>();
			rightProfiles = new HashMap<>();

			for (MotionProfileMap.MotionProfile profile : cfg.getLeftMotionProfileList()) {
				leftProfiles.put(profile.getName(), new MotionProfileData("/home/lvuser/449_resources/" + profile.getFilename(), profile.getInverted()));
			}

			for (MotionProfileMap.MotionProfile profile : cfg.getRightMotionProfileList()) {
				rightProfiles.put(profile.getName(), new MotionProfileData("/home/lvuser/449_resources/" + profile.getFilename(), profile.getInverted()));
			}

			if(cfg.getTestMP()){
				MPLoader.loadTopLevel(leftProfiles.get("test"), driveSubsystem.leftMaster, WHEEL_DIAMETER);
				MPLoader.loadTopLevel(rightProfiles.get("test"), driveSubsystem.rightMaster, WHEEL_DIAMETER);
			} else {
				MPLoader.loadTopLevel(leftProfiles.get(position), driveSubsystem.leftMaster, WHEEL_DIAMETER);
				MPLoader.loadTopLevel(rightProfiles.get(position), driveSubsystem.rightMaster, WHEEL_DIAMETER);
			}

			talons = new ArrayList<>();
			talons.add(driveSubsystem.leftMaster);
			talons.add(driveSubsystem.rightMaster);
			MPNotifier = MPLoader.startLoadBottomLevel(talons, MP_UPDATE_RATE);
			commandFinished = false;
			completedCommands = 0;
		}
	}

	/**
	 * Run when we first enable in teleop.
	 */
	@Override
	public void teleopInit() {
		//Stop the drive for safety reasons
		if (MPNotifier != null) {
			MPNotifier.stop();
		}
		driveSubsystem.setVBusThrottle(0, 0);

		driveSubsystem.leftMaster.canTalon.enable();
		driveSubsystem.rightMaster.canTalon.enable();

		driveSubsystem.setDefaultCommandManual(new DefaultArcadeDrive(driveSubsystem.straightPID, driveSubsystem, oiSubsystem));

//		Scheduler.getInstance().add(new PIDTest(driveSubsystem));
		//Switch to low gear if we have gears
		if (driveSubsystem.shifter != null) {
			if (cfg.getStartLowGear()){
				Scheduler.getInstance().add(new SwitchToLowGear(driveSubsystem));
			} else {
				Scheduler.getInstance().add(new SwitchToHighGear(driveSubsystem));
			}
		}

		if (intakeSubsystem != null) {
			Scheduler.getInstance().add(new IntakeUp(intakeSubsystem));
		}

		if(gearSubsystem != null){
			Scheduler.getInstance().add(new FirePiston(gearSubsystem, DoubleSolenoid.Value.kForward));
		}

		if (robotInfo != null) {
			if (redAlliance) {
				String WriteString = "red_teleop";
				char[] CharArray = WriteString.toCharArray();
				byte[] WriteData = new byte[CharArray.length];
				for (int i = 0; i < CharArray.length; i++) {
					WriteData[i] = (byte) CharArray[i];
				}
				robotInfo.transaction(WriteData, WriteData.length, null, 0);
			} else {
				String WriteString = "blue_teleop";
				char[] CharArray = WriteString.toCharArray();
				byte[] WriteData = new byte[CharArray.length];
				for (int i = 0; i < CharArray.length; i++) {
					WriteData[i] = (byte) CharArray[i];
				}
				robotInfo.transaction(WriteData, WriteData.length, null, 0);
			}
		}
	}

	/**
	 * Run every tick in teleop.
	 */
	@Override
	public void teleopPeriodic() {
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
	}

	/**
	 * Run when we first enable in autonomous
	 */
	@Override
	public void autonomousInit() {
		//Set throttle to 0 for safety reasons
		//Switch to low gear if we have gears
		if (driveSubsystem.shifter != null) {
			if (cfg.getStartLowGear()){
				Scheduler.getInstance().add(new SwitchToLowGear(driveSubsystem));
			} else {
				Scheduler.getInstance().add(new SwitchToHighGear(driveSubsystem));
			}
		}

		if(gearSubsystem != null){
			Scheduler.getInstance().add(new FirePiston(gearSubsystem, DoubleSolenoid.Value.kForward));
		}

		commandFinished = false;

		driveSubsystem.setVBusThrottle(0, 0);

		if (cfg.getDoMP()) {
			if (singleFlywheelShooterSubsystem != null && !cfg.getTestMP()){
				Scheduler.getInstance().add(new AccelerateFlywheel(singleFlywheelShooterSubsystem, 20));
			}
			Scheduler.getInstance().add(new ExecuteProfile(talons, 15, driveSubsystem));

			if (robotInfo != null) {
				if (redAlliance) {
					String WriteString = "red_auto";
					char[] CharArray = WriteString.toCharArray();
					byte[] WriteData = new byte[CharArray.length];
					for (int i = 0; i < CharArray.length; i++) {
						WriteData[i] = (byte) CharArray[i];
					}
					robotInfo.transaction(WriteData, WriteData.length, null, 0);
				} else {
					String WriteString = "blue_auto";
					char[] CharArray = WriteString.toCharArray();
					byte[] WriteData = new byte[CharArray.length];
					for (int i = 0; i < CharArray.length; i++) {
						WriteData[i] = (byte) CharArray[i];
					}
					robotInfo.transaction(WriteData, WriteData.length, null, 0);
				}

			}
		}else {
			Scheduler.getInstance().add(new PIDTest(driveSubsystem, cfg.getDriveBackTime()));
		}
	}

	/**
	 * Runs every tick in autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
		if (cfg.getDoMP() && !cfg.getTestMP()) {
			if (System.currentTimeMillis() - startedGearPush > timeToPushGear && completedCommands == 1) {
				commandFinished = true;
			}
			if (commandFinished) {
				System.out.println("A command finished!");
				completedCommands++;
				commandFinished = false;
				if (completedCommands == 1) {
					if (gearSubsystem != null && dropGear) {
						Scheduler.getInstance().add(new FirePiston(gearSubsystem, DoubleSolenoid.Value.kReverse));
					}
					startedGearPush = System.currentTimeMillis();
				} else if (completedCommands == 2) {
					if (position.equals("center")) {
						Scheduler.getInstance().add(new DriveAtSpeed(driveSubsystem, -0.3, cfg.getDriveBackTime()));
					} else if (position.equals("right") && redAlliance) {
						loadProfile("red_shoot");
						Scheduler.getInstance().add(new ExecuteProfile(talons, 10, driveSubsystem));
					} else if (position.equals("left") && !redAlliance) {
						loadProfile("blue_shoot");
						Scheduler.getInstance().add(new ExecuteProfile(talons, 10, driveSubsystem));
					} else {
						Scheduler.getInstance().add(new DriveAtSpeed(driveSubsystem, -0.3, cfg.getDriveBackTime()));
					}/*else if (redAlliance){
					loadProfile("red_backup");
					Scheduler.getInstance().add(new ExecuteProfile(talons, 10, driveSubsystem));
				} else {
					loadProfile("blue_backup");
					Scheduler.getInstance().add(new ExecuteProfile(talons, 10, driveSubsystem));
				}*/
				} else if (completedCommands == 3) {
					if (((position.equals("right") && redAlliance) || (position.equals("left") && !redAlliance)) && singleFlywheelShooterSubsystem != null) {
						Scheduler.getInstance().add(new FireShooter(singleFlywheelShooterSubsystem, intakeSubsystem, feederSubsystem));
					} else if (!((position.equals("right") && redAlliance) || (position.equals("left") && !redAlliance))) {
						loadProfile("forward");
						Scheduler.getInstance().add(new ExecuteProfile(talons, 10, driveSubsystem));
					}
				}
			}
		}
	}

	@Override
	public void disabledInit(){
		driveSubsystem.setVBusThrottle(0, 0);
	}

	private void loadProfile(String name){
		MPNotifier.stop();
		MPLoader.loadTopLevel(leftProfiles.get(name), driveSubsystem.leftMaster, WHEEL_DIAMETER);
		MPLoader.loadTopLevel(rightProfiles.get(name), driveSubsystem.rightMaster, WHEEL_DIAMETER);
		MPNotifier.startPeriodic(MP_UPDATE_RATE);
	}
}
