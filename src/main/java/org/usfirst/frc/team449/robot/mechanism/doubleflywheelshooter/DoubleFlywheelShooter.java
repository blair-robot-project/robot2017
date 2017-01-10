package org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter;

import org.usfirst.frc.team449.robot.MappedSubsystem;
import org.usfirst.frc.team449.robot.components.CANTalonSRX;

/**
 * Created by blairrobot on 1/10/17.
 */
public class DoubleFlywheelShooter extends MappedSubsystem{

	private CANTalonSRX leftTalon;
	private CANTalonSRX rightTalon;
	public boolean spinning;

	public DoubleFlywheelShooter(maps.org.usfirst.frc.team449.robot.mechanism.doubleflywheelshooter.DoubleFlywheelShooterMap.DoubleFlywheelShooter map){
		super(map.getMechanism());
		this.map = map;
		this.leftTalon = new CANTalonSRX(map.getLeftTalon());
		this.rightTalon = new CANTalonSRX(map.getRightTalon());
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
		leftTalon.setSpeed(1023/leftTalon.canTalon.getF()/60*sp);
		rightTalon.setSpeed(1023/rightTalon.canTalon.getF()/60*sp);
	}

	/**
	 * A wrapper around the speed method we're currently using/testing
	 * @param sp The speed to go at, where 0 is off and 1 is max speed.
	 */
	public void setDefaultSpeed(double sp){
		setVBusSpeed(sp);
	}

	@Override
	protected void initDefaultCommand() {

	}
}
