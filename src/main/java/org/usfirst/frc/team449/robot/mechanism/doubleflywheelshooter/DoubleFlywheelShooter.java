package org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.CANTalonSRX;
import org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.commands.PIDTune;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by blairrobot on 1/10/17.
 */
public class DoubleFlywheelShooter extends MappedSubsystem{

	private CANTalonSRX leftTalon;
	private CANTalonSRX rightTalon;
	public boolean spinning;

	/**
	 * Counts per revolution
	 */
	private final static int CPR = 512;
	
	private long startTime;

	public DoubleFlywheelShooter(maps.org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.DoubleFlywheelShooterMap.DoubleFlywheelShooter map){
		super(map.getMechanism());
		this.map = map;
		this.leftTalon = new CANTalonSRX(map.getLeftTalon());
		this.rightTalon = new CANTalonSRX(map.getRightTalon());
		setPIDF();
	}

	/**
	 * Sets the flywheel to go at a speed between 1 and 0, where 1 is max speed.
	 * @param sp The speed to go at.
	 */
	private void setVBusSpeed(double sp){
		leftTalon.setPercentVbus(sp);
		rightTalon.setPercentVbus(sp);
	}

	private void setPIDSpeed(double sp){
		leftTalon.setSpeed(RPSToNative(sp*leftTalon.getMaxSpeed())/60);
		rightTalon.setSpeed(RPSToNative(sp*rightTalon.getMaxSpeed())/60);
	}

	/**
	 * A wrapper around the speed method we're currently using/testing
	 * @param sp The speed to go at, where 0 is off and 1 is max speed.
	 */
	public void setDefaultSpeed(double sp){
		setVBusSpeed(sp);
	}

	public void setPIDF(){
		rightTalon.canTalon.setF(1023/RPSToNative(rightTalon.getMaxSpeed()));
		leftTalon.canTalon.setF(1023/RPSToNative(leftTalon.getMaxSpeed()));
	}

	public static double nativeToRPS(double nativeUnits){
		return (nativeUnits/(CPR*4))*10;
	}

	public static double RPSToNative(double RPS){
		return (RPS/10)*(CPR*4);
	}

	public void logData(double throttle){
		try (FileWriter fw = new FileWriter("/home/lvuser/driveLog.csv", true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime()-startTime)/100);
			sb.append(",");
			sb.append(nativeToRPS(leftTalon.canTalon.getEncVelocity()));
			sb.append(",");
			SmartDashboard.putNumber("Left", nativeToRPS(leftTalon.canTalon.getEncVelocity()));
			sb.append(nativeToRPS(rightTalon.canTalon.getEncVelocity()));
			sb.append(",");
			SmartDashboard.putNumber("Right", nativeToRPS(rightTalon.canTalon.getEncVelocity()));
			sb.append(throttle);
			sb.append("\n");
			SmartDashboard.putNumber("Throttle", throttle);
			SmartDashboard.putNumber("Left Setpoint", leftTalon.canTalon.getSetpoint());
			SmartDashboard.putNumber("Left Error", leftTalon.canTalon.getError());
			SmartDashboard.putNumber("Right Setpoint", rightTalon.canTalon.getSetpoint());
			SmartDashboard.putNumber("Right Error", rightTalon.canTalon.getError());
			SmartDashboard.putNumber("Left F", leftTalon.canTalon.getF());
			SmartDashboard.putNumber("Right F", rightTalon.canTalon.getF());
			SmartDashboard.putNumber("Right voltage",rightTalon.canTalon.getOutputVoltage());
			SmartDashboard.putNumber("Left voltage",leftTalon.canTalon.getOutputVoltage());
			fw.write(sb.toString());
		}catch (IOException e){
			e.printStackTrace();
		}
	}


	@Override
	protected void initDefaultCommand() {
		try (PrintWriter writer = new PrintWriter("/home/lvuser/shooterLog.csv")) {
			writer.println("Time,Left,Right,Throttle");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		startTime = System.nanoTime();
		setDefaultCommand(new PIDTune(this));
	}
}
