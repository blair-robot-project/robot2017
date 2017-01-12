package org.usfirst.frc.team449.robot.drive.talonCluster;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.CANTalonSRXMap;
import org.usfirst.frc.team449.robot.components.CANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.DriveStraight;
import org.usfirst.frc.team449.robot.oi.OI2017;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side.
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
		setPIDF();
	}

	/**
	 * Sets the left and right wheel speeds as a voltage percentage, not nearly as precise as PID.
	 * @param left The left throttle, a number between -1 and 1 inclusive.
	 * @param right The right throttle, a number between -1 and 1 inclusive.
	 */
	private void setVBusThrottle(double left, double right){
		leftMaster.setPercentVbus(left);
		rightMaster.setPercentVbus(right);
	}

	private void setPIDThrottle(double left, double right){
		leftMaster.setSpeed(RPSToNative(left*leftMaster.getMaxSpeed())/60);
		rightMaster.setSpeed(RPSToNative(right*leftMaster.getMaxSpeed())/60);
	}

	/**
	 * Allows the type of motor control used to be varied in testing.
	 * @param left Left throttle value
	 * @param right Right throttle value
	 */
	public void setDefaultThrottle(double left, double right){
		setPIDThrottle(left, right);
	}

	public static double nativeToRPS(double nativeUnits){
		return (nativeUnits/(512*4))*10; //512 Counts per revolution, 4 edges per count, 10 100ms per second.
	}

	public static double RPSToNative(double rps){
		return (rps/10)*(512*4); //512 Counts per revolution, 4 edges per count, 10 100ms per second.
	}

	public void setPIDF(){
		rightMaster.canTalon.setF(1023/RPSToNative(rightMaster.getMaxSpeed()));
		leftMaster.canTalon.setF(1023/RPSToNative(leftMaster.getMaxSpeed()));
	}

	public void logData(double throttle){
		try (FileWriter fw = new FileWriter("/home/lvuser/driveLog.csv", true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime()-startTime)/100);
			sb.append(",");
			sb.append(nativeToRPS(leftMaster.canTalon.getEncVelocity()));
			sb.append(",");
			SmartDashboard.putNumber("Left", nativeToRPS(leftMaster.canTalon.getEncVelocity()));
			sb.append(nativeToRPS(rightMaster.canTalon.getEncVelocity()));
			sb.append(",");
			SmartDashboard.putNumber("Right", nativeToRPS(rightMaster.canTalon.getEncVelocity()));
			sb.append(throttle);
			sb.append("\n");
			SmartDashboard.putNumber("Throttle", throttle);
			SmartDashboard.putNumber("Left Setpoint", leftMaster.canTalon.getSetpoint());
			SmartDashboard.putNumber("Left Error", leftMaster.canTalon.getError());
			SmartDashboard.putNumber("Right Setpoint", rightMaster.canTalon.getSetpoint());
			SmartDashboard.putNumber("Right Error", rightMaster.canTalon.getError());
			SmartDashboard.putNumber("Left F", leftMaster.canTalon.getF());
			SmartDashboard.putNumber("Right F", rightMaster.canTalon.getF());
			SmartDashboard.putNumber("Right voltage",rightMaster.canTalon.getOutputVoltage());
			SmartDashboard.putNumber("Left voltage",leftMaster.canTalon.getOutputVoltage());
			fw.write(sb.toString());
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	protected void initDefaultCommand() {
		try (PrintWriter writer = new PrintWriter("/home/lvuser/driveLog.csv")) {
			writer.println("Time,Left,Right,Throttle");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		startTime = System.nanoTime();
		setDefaultCommand(new DriveStraight(this, oi));
	}
}
