package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import org.usfirst.frc.team449.robot.mechanism.climber.ClimberSubsystem;
import org.usfirst.frc.team449.robot.oi.OI2017;

import java.io.IOException;

/**
 * Created by BlairRobot on 2017-01-08.
 */
public class Robot extends IterativeRobot {

	public static ClimberSubsystem climberSubsystem;
	private static OI2017 oi;
	private static maps.org.usfirst.frc.team449.robot.Robot2017Map.Robot2017 cfg;

	public void robotInit() {
		try {
			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/map.cfg", Robot2017Map.Robot2017.newBuilder());
		} catch (IOException e) {
			e.printStackTrace();
		}

		oi = new OI2017(cfg.getOi());
		climberSubsystem = new ClimberSubsystem(cfg.getClimber(), oi);
	}

	@Override
	public void teleopInit() {
		System.out.println("Teleop init");
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}
}