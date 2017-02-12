package org.usfirst.frc.team449.robot.drive.talonCluster;

import com.ctre.CANTalon;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import javafx.scene.shape.Arc;
import maps.org.usfirst.frc.team449.robot.components.ToleranceBufferAnglePIDMap;
import maps.org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRXMap;
import org.usfirst.frc.team449.robot.components.NavxSubsystem;
import org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.*;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.ois.*;
import org.usfirst.frc.team449.robot.oi.OI2017;
import org.usfirst.frc.team449.robot.oi.OI2017ArcadeGamepad;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A drive with a cluster of any number of CANTalonSRX controlled motors on each side.
 */
public class TalonClusterDrive extends DriveSubsystem implements NavxSubsystem {

	public UnitlessCANTalonSRX rightMaster;
	public UnitlessCANTalonSRX leftMaster;
	public AHRS navx;
	public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID turnPID;
	public ToleranceBufferAnglePIDMap.ToleranceBufferAnglePID straightPID;
	public OI2017ArcadeGamepad oi;
	public DoubleSolenoid shifter;
	// TODO take this out after testing
	public CANTalon.MotionProfileStatus leftTPointStatus;
	public CANTalon.MotionProfileStatus rightTPointStatus;
	private long startTime;
	private String logFN = "driveLog.csv";
	public boolean overrideNavX;

	private double maxSpeed;
	private final double PID_SCALE = 0.9;

	public TalonClusterDrive(maps.org.usfirst.frc.team449.robot.drive.talonCluster.TalonClusterDriveMap
			                         .TalonClusterDrive map, OI2017ArcadeGamepad oi) {
		super(map.getDrive());
		this.map = map;
		this.oi = oi;
		this.navx = new AHRS(SPI.Port.kMXP);
		this.turnPID = map.getTurnPID();
		this.straightPID = map.getStraightPID();
		if (map.hasShifter()) {
			this.shifter = new DoubleSolenoid(map.getModuleNumber(), map.getShifter().getForward(), map.getShifter().getReverse());
		}
		maxSpeed = -1;

		rightMaster = new UnitlessCANTalonSRX(map.getRightMaster());
		leftMaster = new UnitlessCANTalonSRX(map.getLeftMaster());

		for (UnitlessCANTalonSRXMap.UnitlessCANTalonSRX talon : map.getRightSlaveList()) {
			UnitlessCANTalonSRX talonObject = new UnitlessCANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getRightMaster().getPort());
		}
		for (UnitlessCANTalonSRXMap.UnitlessCANTalonSRX talon : map.getLeftSlaveList()) {
			UnitlessCANTalonSRX talonObject = new UnitlessCANTalonSRX(talon);
			talonObject.canTalon.changeControlMode(CANTalon.TalonControlMode.Follower);
			talonObject.canTalon.set(map.getLeftMaster().getPort());
		}

		// TODO take this out
		leftTPointStatus = new CANTalon.MotionProfileStatus();
		rightTPointStatus = new CANTalon.MotionProfileStatus();
	}

	/**
	 * Sets the left and right wheel speeds as a voltage percentage, not nearly as precise as PID.
	 *
	 * @param left  The left throttle, a number between -1 and 1 inclusive.
	 * @param right The right throttle, a number between -1 and 1 inclusive.
	 */
	private void setVBusThrottle(double left, double right) {
		leftMaster.setPercentVbus(left);
		rightMaster.setPercentVbus(-right);
	}

	private void setPIDThrottle(double left, double right) {
		leftMaster.setSpeed(PID_SCALE * (left * leftMaster.getMaxSpeed()));
		rightMaster.setSpeed(PID_SCALE * (right * rightMaster.getMaxSpeed()));
	}

	/**
	 * Allows the type of motor control used to be varied in testing.
	 *
	 * @param left  Left throttle value
	 * @param right Right throttle value
	 */
	public void setDefaultThrottle(double left, double right) {
		setPIDThrottle(clipToOne(left), clipToOne(right));
	}

	public void logData() {
		try (FileWriter fw = new FileWriter(logFN, true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime() - startTime) / Math.pow(10, 9));
			sb.append(",");
			/*
			sb.append(leftMaster.canTalon.getEncPosition());
			sb.append(",");
			sb.append(rightMaster.canTalon.getEncPosition());
			sb.append(",");
			*/
			sb.append(leftMaster.canTalon.getEncVelocity());
			sb.append(",");
			sb.append(rightMaster.canTalon.getEncVelocity());
			/*
			sb.append(",");
			sb.append(leftTPointStatus.activePoint.position);
			sb.append(",");
			sb.append(rightTPointStatus.activePoint.position);
			*/
			sb.append("\n");

			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		maxSpeed = Math.max(maxSpeed, Math.max(leftMaster.getSpeed(), rightMaster.getSpeed()));
		SmartDashboard.putNumber("Max Speed", maxSpeed);
		SmartDashboard.putNumber("Left", leftMaster.getSpeed());
		SmartDashboard.putNumber("Right", rightMaster.getSpeed());
		SmartDashboard.putNumber("Throttle", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Heading", navx.pidGet());
		SmartDashboard.putNumber("Left Setpoint", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Left Error", leftMaster.nativeToRPS(leftMaster.canTalon.getError()));
		SmartDashboard.putNumber("Right Setpoint", rightMaster.nativeToRPS(rightMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Right Error", rightMaster.nativeToRPS(rightMaster.canTalon.getError()));
		SmartDashboard.putNumber("Left F", leftMaster.canTalon.getF());
		SmartDashboard.putNumber("Right F", rightMaster.canTalon.getF());
		SmartDashboard.putNumber("Left P", leftMaster.canTalon.getP());
		SmartDashboard.putNumber("Right P", rightMaster.canTalon.getP());
	}

	public void logData(double sp) {
		try (FileWriter fw = new FileWriter(logFN, true)) {
			StringBuilder sb = new StringBuilder();
			sb.append((System.nanoTime() - startTime) / Math.pow(10, 9));
			sb.append(",");
			/*
			sb.append(leftMaster.canTalon.getEncPosition());
			sb.append(",");
			sb.append(rightMaster.canTalon.getEncPosition());
			sb.append(",");
			*/
			sb.append(leftMaster.getSpeed());
			sb.append(",");
			sb.append(rightMaster.getSpeed());
			sb.append(",");
			sb.append(PID_SCALE*sp*rightMaster.getMaxSpeed());
			sb.append(",");
			sb.append(rightMaster.getError());
			sb.append(",");
			sb.append(PID_SCALE*sp*leftMaster.getMaxSpeed());
			sb.append(",");
			sb.append(leftMaster.getError());
			/*
			sb.append(",");
			sb.append(leftTPointStatus.activePoint.position);
			sb.append(",");
			sb.append(rightTPointStatus.activePoint.position);
			*/
			sb.append("\n");

			fw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		maxSpeed = Math.max(maxSpeed, Math.max(leftMaster.getSpeed(), rightMaster.getSpeed()));
		SmartDashboard.putNumber("Max Speed", maxSpeed);
		SmartDashboard.putNumber("Left", leftMaster.getSpeed());
		SmartDashboard.putNumber("Right", rightMaster.getSpeed());
		SmartDashboard.putNumber("Throttle", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Heading", navx.pidGet());
		SmartDashboard.putNumber("Left Setpoint", leftMaster.nativeToRPS(leftMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Left Error", leftMaster.nativeToRPS(leftMaster.canTalon.getError()));
		SmartDashboard.putNumber("Right Setpoint", rightMaster.nativeToRPS(rightMaster.canTalon.getSetpoint()));
		SmartDashboard.putNumber("Right Error", rightMaster.nativeToRPS(rightMaster.canTalon.getError()));
		SmartDashboard.putNumber("Left F", leftMaster.canTalon.getF());
		SmartDashboard.putNumber("Right F", rightMaster.canTalon.getF());
		SmartDashboard.putNumber("Left P", leftMaster.canTalon.getP());
		SmartDashboard.putNumber("Right P", rightMaster.canTalon.getP());
	}

	@Override
	protected void initDefaultCommand() {
		logFN = "/home/lvuser/logs/driveLog-" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".csv";
		try (PrintWriter writer = new PrintWriter(logFN)) {
			writer.println("time,left,right,right setpoint,right error,left setpoint,left error");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
	}


//		setDefaultCommand(new ExecuteProfile(this));
//		setDefaultCommand(new OpTankDrive(this, oi));

		startTime = System.nanoTime();
		overrideNavX = false;
		//setDefaultCommand(new ExecuteProfile(this));
		setDefaultCommand(new DefaultArcadeDrive(straightPID, this, oi));
	}

	public double getGyroOutput() {
		return navx.pidGet();
	}

	public void setLowGear(boolean setLowGear){
		if (shifter != null) {
			if (setLowGear) {
				shifter.set(DoubleSolenoid.Value.kForward);
				rightMaster.switchToLowGear();
				leftMaster.switchToLowGear();
			} else {
				shifter.set(DoubleSolenoid.Value.kReverse);
				rightMaster.switchToHighGear();
				leftMaster.switchToHighGear();
			}
		} else {
			System.out.println("You're trying to shift gears, but your drive doesn't have a shifter.");
		}
	}

	/**
	 * Simple helper function for clipping output to the -1 to 1 scale.
	 * @param in The number to be processed.
	 * @return That number, clipped to 1 if it's greater than 1 or clipped to -1 if it's less than -1.
	 */
	private static double clipToOne(double in){
		if (in > 1)
			return 1;
		else if (in < -1)
			return -1;
		else
			return in;
	}
}
