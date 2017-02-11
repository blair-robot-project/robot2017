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
import org.usfirst.frc.team449.robot.mechanism.intake.Intake2017;
import org.usfirst.frc.team449.robot.mechanism.pneumatics.PneumaticsSubsystem;
import org.usfirst.frc.team449.robot.mechanism.singleflywheelshooter.SingleFlywheelShooter;
import org.usfirst.frc.team449.robot.oi.BaseOI;
import org.usfirst.frc.team449.robot.oi.OI2017Tank;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;

import java.io.IOException;

/**
 * Created by BlairRobot on 2017-01-08.
 */
public class Robot extends IterativeRobot {

	public static DoubleFlywheelShooter doubleFlywheelShooterSubsystem;
	public static SingleFlywheelShooter singleFlywheelShooterSubsystem;
	public static Intake2017 intakeSubsystem;

	public static ClimberSubsystem climberSubsystem;

	public static PneumaticsSubsystem pneumaticsSubsystem;

	public static TalonClusterDrive driveSubsystem;

	public static OI2017ArcadeGamepad oiSubsystem;

	private static maps.org.usfirst.frc.team449.robot.Robot2017Map.Robot2017 cfg;

	public void robotInit() {
		System.out.println("Started robotInit");
		try {
			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/449_resources/final_map.cfg",
					Robot2017Map.Robot2017.newBuilder());
		} catch (IOException e) {
			System.out.println("Config file not found!");
			e.printStackTrace();
		}

		oiSubsystem = new OI2017ArcadeGamepad(cfg.getArcadeOi());
		System.out.println("Constructed OI");

		driveSubsystem = new TalonClusterDrive(cfg.getDrive(), oiSubsystem);

		System.out.println("Constructed drive");

		//		climberSubsystem = new ClimberSubsystem(cfg.getClimber(), oiSubsystem);
		//		doubleFlywheelShooterSubsystem = new DoubleFlywheelShooter(cfg.getDoubleFlywheelShooter());
		//		singleFlywheelShooterSubsystem = new SingleFlywheelShooter(cfg.getShooter());
		//		System.out.println("Constructed SingleFlywheelShooter");
		//		shooterSubsystem = new DoubleFlywheelShooter(cfg.getShooter());
		//		System.out.println("Constructed DoubleFlywheelShooter");
		//		pneumaticsSubsystem = new PneumaticsSubsystem(cfg.getPneumatics());
		//		System.out.println("Constructed PneumaticsSubsystem");

		//intakeSubsystem = new Intake2017(cfg.getIntake(), oiSubsystem);

		oiSubsystem.mapButtons();

		Compressor compressor = new Compressor(15);
		compressor.setClosedLoopControl(true);
		compressor.start();

		System.out.println("Mapped buttons");
	}

	@Override
	public void teleopInit() {
		Scheduler.getInstance().add(new SwitchToLowGear(driveSubsystem));
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