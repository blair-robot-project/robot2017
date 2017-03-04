package org.usfirst.frc.team449.robot.mechanism.feeder;

import edu.wpi.first.wpilibj.VictorSP;
import maps.org.usfirst.frc.team449.robot.mechanism.feeder.FeederMap;
import org.usfirst.frc.team449.robot.mechanism.MechanismSubsystem;

/**
 * The auger that feeds balls into the shooter.
 */
public class FeederSubsystem extends MechanismSubsystem {

	/**
	 * The Victor motor that runs the auger
	 */
	private VictorSP victor;

	/**
	 * The percentage speed for the motor to run at, from -1 to 1.
	 */
	private double speed;

	/**
	 * Whether or not the motor is currently running.
	 */
	public boolean running;

	/**
	 * Construct a FeederSubsystem
	 * @param map The config map
	 */
	public FeederSubsystem(FeederMap.Feeder map) {
		super(map.getMechanism());
		speed = map.getSpeed();

		//TODO make a MappedVictor class that we can directly give a motor map.
		//Initialize the Victor
		this.victor = new VictorSP(map.getVictor().getPort());
		victor.setInverted(map.getVictor().getInverted());

		//Starts off
		running = false;
	}

	/**
	 * Turns the motor on.
	 */
	public void runVictor() {
		victor.set(speed);
		running = true;
	}

	/**
	 * Turns the motor off.
	 */
	public void stopVictor() {
		victor.set(0);
		running = false;
	}

	/**
	 * Initialize the default command for a subsystem By default subsystems have
	 * no default command, but if they do, the default command is set with this
	 * method. It is called on all Subsystems by CommandBase in the users program
	 * after all the Subsystems are created.
	 */
	@Override
	protected void initDefaultCommand() {
		//Do nothing! Inheritance is dumb sometimes.
	}
}
