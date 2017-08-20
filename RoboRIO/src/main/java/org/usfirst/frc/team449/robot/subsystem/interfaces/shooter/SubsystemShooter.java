package org.usfirst.frc.team449.robot.subsystem.interfaces.shooter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;

/**
 * A subsystem with a shooter and feeder.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemShooter {

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
	 * @return The current state of the shooter.
	 */
	@NotNull
	ShooterState getShooterState();

	/**
	 * @param state The state to switch the shooter to.
	 */
	void setShooterState(@NotNull ShooterState state);

	/**
	 * @return Time from giving the shooter voltage to being ready to fire, in milliseconds.
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
