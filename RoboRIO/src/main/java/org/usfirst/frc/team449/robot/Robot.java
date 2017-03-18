package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.PIDTest;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToLowGear;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017.commands.updown.IntakeUp;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

import java.io.IOException;

/**
 * The main class of the robot, constructs all the subsystems and initializes default commands.
 */
public class Robot extends IterativeRobot {

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

	/**
	 * The object constructed directly from map.cfg.
	 * */
	private static Robot2017Map.Robot2017 cfg;

	/**
	 * The method that runs when the robot is turned on. Initializes all subsystems from the map.
	 */
	public void robotInit() {
		System.out.println("Started robotInit.");

		try {
			//Try to construct map from the cfg file
			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/balbasaur_map.cfg",
			//cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/final_map.cfg",
			//cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/fancy_map.cfg",
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

		//Map the buttons (has to be done last because all the subsystems need to have been instantiated.)
		oiSubsystem.mapButtons();
		System.out.println("Mapped buttons");

		//Activate the compressor if its module number is in the map.
		if (cfg.hasModule()) {
			Compressor compressor = new Compressor(cfg.getModule());
			compressor.setClosedLoopControl(true);
			compressor.start();
		}
	}

	/**
	 * Run when we first enable in teleop.
	 */
	@Override
	public void teleopInit() {
		//Stop the drive for safety reasons
		driveSubsystem.setVBusThrottle(0, 0);

		driveSubsystem.leftMaster.canTalon.enable();
		driveSubsystem.rightMaster.canTalon.enable();

		//Switch to low gear if we have gears
		if (driveSubsystem.shifter != null) {
			Scheduler.getInstance().add(new SwitchToLowGear(driveSubsystem));
		}

		if (intakeSubsystem != null) {
			Scheduler.getInstance().add(new IntakeUp(intakeSubsystem));
		}

//		Scheduler.getInstance().add(new DefaultArcadeDrive(driveSubsystem.straightPID, driveSubsystem, oiSubsystem));
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
		driveSubsystem.leftMaster.canTalon.enable();
		driveSubsystem.rightMaster.canTalon.enable();
		driveSubsystem.setVBusThrottle(0, 0);
		Scheduler.getInstance().add(new PIDTest(driveSubsystem));
//		Scheduler.getInstance().add(new ExecuteProfile(driveSubsystem));
	}

	/**
	 * Runs every tick in autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//Run all commands. This is a WPILib thing you don't really have to worry about.
		Scheduler.getInstance().run();
		SmartDashboard.putNumber("Heading", driveSubsystem.getGyroOutput());
	}
}