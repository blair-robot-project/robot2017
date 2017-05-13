package org.usfirst.frc.team449.robot.components;

import edu.wpi.first.wpilibj.VictorSP;
import maps.org.usfirst.frc.team449.robot.components.MotorMap;

/**
 * Created by noah on 5/12/17.
 */
public class MappedVictor extends VictorSP{

	public MappedVictor(MotorMap.Motor map){
		super(map.getPort());
		this.setInverted(map.getInverted());
	}
}
