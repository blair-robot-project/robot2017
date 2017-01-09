package org.usfirst.frc.team449.robot.drive.talonCluster;

import edu.wpi.first.wpilibj.CANTalon;
import maps.org.usfirst.frc.team449.robot.components.CANTalonSRXMap;
import org.usfirst.frc.team449.robot.components.CANTalonSRX;
import org.usfirst.frc.team449.robot.drive.DriveSubsystem;
import org.usfirst.frc.team449.robot.drive.talonCluster.commands.DefaultDrive;
import org.usfirst.frc.team449.robot.oi.OI2017;
import org.usfirst.frc.team449.robot.oi.OISubsystem;

/**
 * Created by BlairRobot on 2017-01-08.
 */
public class TalonClusterDrive extends DriveSubsystem {

	public CANTalonSRX rightMaster;
	public CANTalonSRX leftMaster;

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

	/**
	 * Allows the type of motor control used to be varied in testing.
	 * @param left Left throttle value
	 * @param right Right throttle value
	 */
	public void setDefaultThrottle(double left, double right){
		setVBusThrottle(left, right);
	}

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new DefaultDrive(this, oi));
	}
}
