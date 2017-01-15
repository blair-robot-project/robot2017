package org.usfirst.frc.team449.robot.mechanism.climber;

import edu.wpi.first.wpilibj.CANTalon;
import org.usfirst.frc.team449.robot.components.CANTalonSRX;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;
import org.usfirst.frc.team449.robot.mechanism.climber.commands.Climb;
import org.usfirst.frc.team449.robot.oi.OI2017;

/**
 * Created by Justin on 1/12/2017.
 */
public class ClimberSubsystem extends MechanismSubsystem {

	CANTalonSRX canTalonSRX;
	OI2017 oi;

	public ClimberSubsystem(maps.org.usfirst.frc.team449.robot.mechanism.climber.ClimberMap.Climber map, OI2017 oi) {
		super(map.getMechanism());
		this.map=map;
		canTalonSRX=new CANTalonSRX(map.getWinch()){};
		this.oi=oi;
	}

	/**
	 * Initialize the default command for a subsystem By default subsystems have
	 * no default command, but if they do, the default command is set with this
	 * method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new Climb(this, oi));
	}

	public void setPercentVbus(double percentVbus) {
		canTalonSRX.setPercentVbus(percentVbus);
	}

	public void setControlMode(CANTalon.TalonControlMode mode){
		canTalonSRX.canTalon.changeControlMode(mode);
	}
}