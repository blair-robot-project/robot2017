package org.usfirst.frc.team449.robot.subsystem.interfaces.shooter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.jetbrains.annotations.NotNull;

/**
 * A subsystem with a multiSubsystem and feeder.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_OBJECT, property = "@class")
public interface SubsystemShooter {

	/**
	 * Turn the multiSubsystem on to a map-specified speed.
	 */
	void turnShooterOn();

	/**
	 * Turn the multiSubsystem off.
	 */
	void turnShooterOff();

	/**
	 * Start feeding balls into the multiSubsystem.
	 */
	void turnFeederOn();

	/**
	 * Stop feeding balls into the multiSubsystem.
	 */
	void turnFeederOff();

	/**
	 * @return The current state of the multiSubsystem.
	 */
	@NotNull
	ShooterState getShooterState();

	/**
	 * @param state The state to switch the multiSubsystem to.
	 */
	void setShooterState(@NotNull ShooterState state);

	/**
	 * @return Time from giving the multiSubsystem voltage to being ready to fire, in milliseconds.
	 */
	long getSpinUpTimeMillis();

	/**
	 * An enum for the possible states of the multiSubsystem.
	 */
	enum ShooterState {
		//Both multiSubsystem and feeder off
		OFF,
		//Feeder off, multiSubsystem on
		SPINNING_UP,
		//Both multiSubsystem and feeder on
		SHOOTING
	}
}
