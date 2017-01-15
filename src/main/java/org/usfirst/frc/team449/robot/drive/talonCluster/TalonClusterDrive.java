package org.usfirst.frc.team449.robot.drive.talonCluster;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import maps.org.usfirst.frc.team449.robot.components.AnglePIDMap;
import maps.org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRXMap;
import org.usfirst.frc.team449.robot.components.NavxSubsystem;
import org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.DefaultDrive;
import org.usfirst.frc.team449.robot.oi.OI2017;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side.
 */
public class TalonClusterDrive extends DriveSubsystem implements NavxSubsystem{

	public UnitlessCANTalonSRX rightMaster;
	public UnitlessCANTalonSRX leftMaster;

	private long startTime;
	public AHRS navx;
	public AnglePIDMap.AnglePID turnPID;
	public AnglePIDMap.AnglePID straightPID;

	public OI2017 oi;

	public TalonClusterDrive(maps.org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDriveMap.TalonClusterDrive map, OI2017 oi){
		super(map.getDrive());
		this.map = map;
		this.oi = oi;
		this.navx = new AHRS(SPI.Port.kMXP);
		this.turnPID = map.getTurnPID();
		this.straightPID = map.getStraightPID();

		rightMaster = new UnitlessCANTalonSRX(map.getRightMaster());
		leftMaster = new UnitlessCANTalonSRX(map.getLeftMaster());

		/*
		rightMaster = new UnitlessCANTalonSRX(map.getLeftMaster());
		leftMaster = new UnitlessCANTalonSRX(map.getRightMaster());
		*/
		for (UnitlessCANTalonSRXMap.UnitlessCANTalonSRX talon : map.getRightSlaveList()){
			UnitlessCANTalonSRX talonObject = new UnitlessCANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getRightMaster().getPort());
		}
		for (UnitlessCANTalonSRXMap.UnitlessCANTalonSRX talon : map.getLeftSlaveList()){
			UnitlessCANTalonSRX talonObject = new UnitlessCANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getLeftMaster().getPort());
		}
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
		leftMaster.setSpeed(.7*(left*leftMaster.getMaxSpeed()));
		rightMaster.setSpeed(.7*(right*rightMaster.getMaxSpeed()));
	}

	/**
	 * Allows the type of motor control used to be varied in testing.
	 * @param left Left throttle value
	 * @param right Right throttle value
	 */
	public void setDefaultThrottle(double left, double right){
		setPIDThrottle(left, right);
	}

	public void logData(){
		try (FileWriter fw = new FileWriter("/home/lvuser/driveLog.csv", true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime()-startTime)/100);
			sb.append(",");
			sb.append(leftMaster.getSpeed());
			sb.append(",");
			SmartDashboard.putNumber("Left", leftMaster.getSpeed());
			sb.append(rightMaster.getSpeed());
			sb.append(",");
			SmartDashboard.putNumber("Right", rightMaster.getSpeed());
			sb.append(leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
			sb.append("\n");
			SmartDashboard.putNumber("Throttle", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
			SmartDashboard.putNumber("Heading", navx.pidGet());
			SmartDashboard.putNumber("Left Setpoint", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
			SmartDashboard.putNumber("Left Error", leftMaster.nativeToRPS(leftMaster.canTalon.getError()));
			SmartDashboard.putNumber("Right Setpoint", rightMaster.nativeToRPS(rightMaster.canTalon.getSetpoint()));
			SmartDashboard.putNumber("Right Error", rightMaster.nativeToRPS(rightMaster.canTalon.getError()));
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
		setDefaultCommand(new DefaultDrive(this, oi));
	}

	public double getGyroOutput(){
		return navx.pidGet();
	}
}
