package org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter;

/**
 * Created by noah on 5/20/17.
 */
public interface ShooterSubsystem {
	/**
	 * Turn the shooter on to a map-specified speed.
	 */
	void turnShooterOn();

	/**
	 * Turn the shooter off.
	 */
	void turnShooterOff();

	/**
	 * Start feeding balls into the shooter.
	 */
	void turnFeederOn();

	/**
	 * Stop feeding balls into the shooter.
	 */
	void turnFeederOff();

	/**
	 * Sets the state of the shooter. Only called from within commands.
	 * @param state Off, spinning up, or shooting
	 */
	void setShooterState(ShooterState state);

	/**
	 * Gets the shooter's state, for use in "toggle" commands.
	 * @return Off, spinning up, or shooting.
	 */
	ShooterState getShooterState();

	/**
	 * How long it takes for the shooter to get up to launch speed. Should be measured experimentally.
	 * @return Time from giving the shooter a voltage to being ready to fire, in milliseconds.
	 */
	long getSpinUpTimeMillis();

	/**
	 * An enum for the possible states of the shooter.
	 */
	enum ShooterState {
		//Both shooter and feeder off
		OFF,
		//Feeder off, shooter on
		SPINNING_UP,
		//Both shooter and feeder on
		SHOOTING
	}
}
