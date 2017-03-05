package org.usfirst.frc.team449.robot.mechanism.climber;

import org.usfirst.frc.team449.robot.components.UnitlessCANTalonSRX;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;

/**
 * Created by Justin on 1/12/2017.
 */
public class ClimberSubsystem extends MechanismSubsystem {
	public UnitlessCANTalonSRX canTalonSRX;
	private double max_current;

	public ClimberSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.climber.ClimberMap.Climber map) {
		super(map.getMechanism());
		this.map = map;
		canTalonSRX = new UnitlessCANTalonSRX(map.getWinch());
		this.max_current = map.getMaxCurrent();
	}

	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}

	public void setPercentVbus(double percentVbus) {
		canTalonSRX.setPercentVbus(percentVbus);
	}

	public boolean reachedTop() {
		return canTalonSRX.canTalon.getOutputCurrent() > max_current;
	}
}