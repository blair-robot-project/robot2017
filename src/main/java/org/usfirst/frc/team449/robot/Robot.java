package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import maps.org.usfirst.frc.team449.robot.components.MotionProfileMap;
import org.usfirst.frc.team449.robot.components.RotPerSecCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.DefaultArcadeDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.DriveAtSpeed;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ExecuteProfile;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.OpArcadeDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.PIDTest;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToHighGear;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToLowGear;
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
import java.util.List;

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

	private Boolean commandFinished = false;

	private int completedCommands = 0;

	private long startedGearPush = 0;

	private long timeToPushGear;

	private List<MotionProfileData> rightProfiles, leftProfiles;

	private List<RotPerSecCANTalonSRX> talons;

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

		WHEEL_DIAMETER = cfg.getWheelDiameterInches()/12.;
		timeToPushGear = (long) (cfg.getTimeToPushGear()*1000);

		leftProfiles = new ArrayList<>(cfg.getLeftMotionProfileCount());
		for (int i = 0; i < cfg.getLeftMotionProfileCount(); i++)
			leftProfiles.add(null);
		rightProfiles = new ArrayList<>(cfg.getRightMotionProfileCount());
		for (int i = 0; i < cfg.getRightMotionProfileCount(); i++)
			rightProfiles.add(null);

		for (MotionProfileMap.MotionProfile profile : cfg.getLeftMotionProfileList()){
			leftProfiles.set(profile.getNumber(), new MotionProfileData("/home/lvuser/449_resources/"+profile.getFilename(), profile.getInverted()));
		}

		for (MotionProfileMap.MotionProfile profile : cfg.getRightMotionProfileList()){
			rightProfiles.set(profile.getNumber(), new MotionProfileData("/home/lvuser/449_resources/"+profile.getFilename(), profile.getInverted()));
		}

		MPLoader.loadTopLevel(leftProfiles.get(0), driveSubsystem.leftMaster, WHEEL_DIAMETER);
		MPLoader.loadTopLevel(rightProfiles.get(0), driveSubsystem.rightMaster, WHEEL_DIAMETER);

		talons = new ArrayList<>();
		talons.add(driveSubsystem.leftMaster);
		talons.add(driveSubsystem.rightMaster);
		MPNotifier = MPLoader.startLoadBottomLevel(talons, MP_UPDATE_RATE);
		commandFinished = false;
		completedCommands = 0;
	}

	/**
	 * Run when we first enable in teleop.
	 */
	@Override
	public void teleopInit() {
		//Stop the drive for safety reasons
		MPNotifier.stop();
		driveSubsystem.setVBusThrottle(0, 0);

		driveSubsystem.leftMaster.canTalon.enable();
		driveSubsystem.rightMaster.canTalon.enable();

//		driveSubsystem.setDefaultCommandManual(new OpArcadeDrive(driveSubsystem, oiSubsystem));

		Scheduler.getInstance().add(new PIDTest(driveSubsystem));
		//Switch to low gear if we have gears
		if (driveSubsystem.shifter != null) {
			Scheduler.getInstance().add(new SwitchToLowGear(driveSubsystem));
		}

		if (intakeSubsystem != null) {
			Scheduler.getInstance().add(new IntakeUp(intakeSubsystem));
		}

		if(gearSubsystem != null){
			Scheduler.getInstance().add(new FirePiston(gearSubsystem, DoubleSolenoid.Value.kForward));
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
		if (driveSubsystem.shifter != null) {
			Scheduler.getInstance().add(new SwitchToLowGear(driveSubsystem));
		}
		if (singleFlywheelShooterSubsystem != null){
			Scheduler.getInstance().add(new AccelerateFlywheel(singleFlywheelShooterSubsystem, 20));
		}
		if(gearSubsystem != null){
			Scheduler.getInstance().add(new FirePiston(gearSubsystem, DoubleSolenoid.Value.kForward));
		}

		driveSubsystem.setVBusThrottle(0, 0);
		List<RotPerSecCANTalonSRX> talons = new ArrayList<>();
		talons.add(driveSubsystem.leftMaster);
		talons.add(driveSubsystem.rightMaster);
		Scheduler.getInstance().add(new ExecuteProfile(talons, 15, driveSubsystem, commandFinished));
	}

	/**
	 * Runs every tick in autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
		if (System.currentTimeMillis() - startedGearPush > timeToPushGear && completedCommands == 1){
			commandFinished = true;
		}
		if(commandFinished){
			completedCommands++;
			commandFinished = false;
			if(completedCommands == 1){
				if(gearSubsystem != null) {
					Scheduler.getInstance().add(new FirePiston(gearSubsystem, DoubleSolenoid.Value.kReverse));
				}
				startedGearPush = System.currentTimeMillis();
			} else if (completedCommands == 2){
				if(leftProfiles.size() >= 2) {
					loadProfile(1);
					Scheduler.getInstance().add(new ExecuteProfile(talons, 10, driveSubsystem, commandFinished));
				} else {
					Scheduler.getInstance().add(new DriveAtSpeed(driveSubsystem, -0.3, .5));
				}
			} else if (completedCommands == 3){
				if (leftProfiles.size() >= 3){
					loadProfile(2);
					Scheduler.getInstance().add(new ExecuteProfile(talons, 10, driveSubsystem, commandFinished));
				} else {
					Scheduler.getInstance().add(new FireShooter(singleFlywheelShooterSubsystem, intakeSubsystem, feederSubsystem));
				}
			}
		}
	}

	@Override
	public void disabledInit(){
		driveSubsystem.setVBusThrottle(0, 0);
	}

	private void loadProfile(int index){
		MPNotifier.stop();
		MPLoader.loadTopLevel(leftProfiles.get(index), driveSubsystem.leftMaster, WHEEL_DIAMETER);
		MPLoader.loadTopLevel(rightProfiles.get(index), driveSubsystem.rightMaster, WHEEL_DIAMETER);
		MPNotifier.startPeriodic(MP_UPDATE_RATE);
	}
}