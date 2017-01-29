package org.usfirst.frc.team449.robot.mechanism.climber;

import org.usfirst.frc.team449.robot.components.CANTalonSRX;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;

/**
 * Created by Justin on 1/12/2017.
 */
public class ClimberSubsystem extends MechanismSubsystem {
	CANTalonSRX canTalonSRX;

	public ClimberSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.climber.ClimberMap.Climber map) {
		super(map.getMechanism());
		this.map = map;
		canTalonSRX = new CANTalonSRX(map.getWinch());
	}

	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	public void setPercentVbus(double percentVbus) {
		canTalonSRX.setPercentVbus(percentVbus);
	}
}