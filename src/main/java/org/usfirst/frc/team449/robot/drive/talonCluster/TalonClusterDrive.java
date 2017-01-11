package org.usfirst.frc.team449.robot.drive.talonCluster;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.CANTalonSRXMap;
import org.usfirst.frc.team449.robot.components.CANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.DefaultDrive;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.DriveStraight;
import org.usfirst.frc.team449.robot.oi.OI2017;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by BlairRobot on 2017-01-08.
 */
public class TalonClusterDrive extends DriveSubsystem {

	public CANTalonSRX rightMaster;
	public CANTalonSRX leftMaster;

	private long startTime;

	public OI2017 oi;

	public TalonClusterDrive(maps.org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDriveMap.TalonClusterDrive map, OI2017 oi){
		super(map.getDrive());
		this.map = map;
		this.oi = oi;
		rightMaster = new CANTalonSRX(map.getRightMaster());
		leftMaster = new CANTalonSRX(map.getLeftMaster());
		for (CANTalonSRXMap.CANTalonSRX talon : map.getRightSlaveList()){
			CANTalonSRX talonObject = new CANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getRightMaster().getPort());
		}
		for (CANTalonSRXMap.CANTalonSRX talon : map.getLeftSlaveList()){
			CANTalonSRX talonObject = new CANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getLeftMaster().getPort());
		}
	}

	/**
	 * Sets the left and right wheel speeds as a voltage percentage, not nearly as precise as PID.
	 * @param left The left throttle, a number between -1 and 1 inclusive.
	 * @param right The right throttle, a number between -1 and 1 inclusive.
	 */
	public void setVBusThrottle(double left, double right){
		leftMaster.setPercentVbus(left);
		rightMaster.setPercentVbus(right);
	}

	public void setPIDThrottle(double left, double right){
		leftMaster.setSpeed(1023/leftMaster.canTalon.getF()/60*left);
		rightMaster.setSpeed(1023/rightMaster.canTalon.getF()/60*right);
	}

	/**
	 * Allows the type of motor control used to be varied in testing.
	 * @param left Left throttle value
	 * @param right Right throttle value
	 */
	public void setDefaultThrottle(double left, double right){
		setVBusThrottle(left, right);
	}

	public static double nativeToRPS(double nativeUnits){
		return (nativeUnits/(512*4))*10; //512 Counts per revolution, 4 edges per count, 10 100ms per second.
	}

	public static double RPSToNative(double rps){
		return (rps/10)*(512*4); //512 Counts per revolution, 4 edges per count, 10 100ms per second.
	}

	public void logData(double throttle){
		try (FileWriter fw = new FileWriter("/home/lvuser/shooterLog.csv", true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime()-startTime)/100);
			sb.append(",");
			sb.append(leftMaster.canTalon.getEncVelocity());
			sb.append(",");
			SmartDashboard.putNumber("Left",leftMaster.canTalon.getEncVelocity());
			sb.append(rightMaster.canTalon.getEncVelocity());
			sb.append(",");
			SmartDashboard.putNumber("Right", rightMaster.canTalon.getEncVelocity());
			sb.append(throttle);
			sb.append("\n");
			fw.write(sb.toString());
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	protected void initDefaultCommand() {
		startTime = System.nanoTime();
		setDefaultCommand(new DriveStraight(this, oi));
	}
}
