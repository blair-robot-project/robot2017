package org.usfirst.frc.team449.robot.mechanism.feeder;

import edu.wpi.first.wpilibj.VictorSP;
import maps.org.usfirst.frc.team449.robot.mechanism.feeder.FeederMap;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;

/**
 * Created by Noah Gleason on 2/18/2017.
 */
public class FeederSubsystem extends MechanismSubsystem {

	private VictorSP victor;
	private double speed;
	public boolean running;

	public FeederSubsystem(FeederMap.Feeder map) {
		super(map.getMechanism());
		this.victor = new VictorSP(map.getVictor().getPort());
		victor.setInverted(map.getVictor().getInverted());
		speed = map.getSpeed();
		running = false;
	}

	public void runVictor() {
		victor.set(speed);
		running = true;
	}

	public void stopVictor() {
		victor.set(0);
		running = false;
	}

	@Override
	protected void initDefaultCommand() {
		//Do nothing!
	}
}
