package org.usfirst.frc.team449.robot;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import maps.org.usfirst.frc.team449.robot.Robot2017Map;
import maps.org.usfirst.frc.team449.robot.oi.OIMap;
import org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.util.MotionProfileData;
import org.usfirst.frc.team449.robot.oi.OI2017;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

import java.io.IOException;

/**
 * Created by BlairRobot on 2017-01-08.
 */
public class Robot extends IterativeRobot {

    public static TalonClusterDrive driveSubsystem;

    public static OI2017 oiSubsystem;

	private static maps.org.usfirst.frc.team449.robot.Robot2017Map.Robot2017 cfg;

	public void robotInit(){
		try {
			cfg = (Robot2017Map.Robot2017) MappedSubsystem.readConfig("/home/lvuser/map.cfg", Robot2017Map.Robot2017.newBuilder());
		} catch (IOException e) {
			e.printStackTrace();
		}
		oiSubsystem = new OI2017(cfg.getOi());
		driveSubsystem = new TalonClusterDrive(cfg.getDrive(), oiSubsystem);
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}
}