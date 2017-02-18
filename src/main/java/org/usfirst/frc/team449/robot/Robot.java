package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import maps.org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticSystemMap;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToHighGear;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.SwitchToLowGear;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.DoubleFlywheelShooter;
import org.usfirst.frc.team449.robot.mechanism.feeder.FeederSubsystem;
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.oi.BaseOI;
import org.usfirst.frc.team449.robot.oi.OI2017Tank;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;
import org.usfirst.frc.team449.robot.vision.CameraSubsystem;

import java.io.IOException;

/**
 * Created by BlairRobot on 2017-01-08.
 */
public class Robot extends IterativeRobot {

	public static SingleFlywheelShooter singleFlywheelShooterSubsystem;

	public static Intake2017 intakeSubsystem;

	public static ClimberSubsystem climberSubsystem;

	public static PneumaticsSubsystem pneumaticsSubsystem;

	public static TalonClusterDrive driveSubsystem;

	public static OI2017ArcadeGamepad oiSubsystem;

	public static CameraSubsystem cameraSubsystem;

	public static FeederSubsystem feederSubsystem;

	private static maps.org.usfirst.frc.team449.robot.Robot2017Map.Robot2017 cfg;

	public void robotInit() {
		System.out.println("Started robotInit");
		try {
			//cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/balbasaur_map.cfg",
			//cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/final_map.cfg",
			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/final_map_only_drive.cfg",
					Robot2017Map.Robot2017.newBuilder());
		} catch (IOException e) {
			System.out.println("Config file is bad/nonexistent!");
			e.printStackTrace();
		}

		oiSubsystem = new OI2017ArcadeGamepad(cfg.getArcadeOi());
		System.out.println("Constructed OI");

		driveSubsystem = new TalonClusterDrive(cfg.getDrive(), oiSubsystem);
		System.out.println("Constructed Drive");

		if (cfg.hasCamera()) {
			cameraSubsystem = new CameraSubsystem(cfg.getCamera());
		}

		if (cfg.hasClimber()) {
			climberSubsystem = new ClimberSubsystem(cfg.getClimber());
		}

		if (cfg.hasShooter()) {
			singleFlywheelShooterSubsystem = new SingleFlywheelShooter(cfg.getShooter());
			System.out.println("Constructed SingleFlywheelShooter");
		}

		if (cfg.hasPneumatics()) {
			pneumaticsSubsystem = new PneumaticsSubsystem(cfg.getPneumatics());
			System.out.println("Constructed PneumaticsSubsystem");
		}

		if (cfg.hasIntake()) {
			intakeSubsystem = new Intake2017(cfg.getIntake());
		}

		if (cfg.hasFeeder()){
			feederSubsystem = new FeederSubsystem(cfg.getFeeder());
		}

		oiSubsystem.mapButtons();
		System.out.println("Mapped buttons");

		if (cfg.hasModule()) {
			Compressor compressor = new Compressor(cfg.getModule());
			compressor.setClosedLoopControl(true);
			compressor.start();
		}
	}

	@Override
	public void teleopInit() {
		if (driveSubsystem.shifter != null) {
			Scheduler.getInstance().add(new SwitchToLowGear(driveSubsystem));
		}
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void autonomousInit() {
//		Scheduler.getInstance().add(new ExecuteProfile(driveSubsystem));
	}

	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}
}