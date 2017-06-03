package org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Shooter.ShooterSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Turn off the shooter and feeder, using requires() to interrupt any other commands that may be telling them to
 * continue running.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class TurnAllOffWithRequires extends Command {

	/**
	 * The subsystem to execute this command on.
	 */
	private ShooterSubsystem subsystem;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 */
	@JsonCreator
	public <T extends Subsystem & ShooterSubsystem> TurnAllOffWithRequires(@JsonProperty(required = true) T subsystem) {
		requires(subsystem);
		this.subsystem = subsystem;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("TurnAllOffWithRequires init.", this.getClass());
	}

	/**
	 * Turn off the shooter and feeder.
	 */
	@Override
	protected void execute() {
		subsystem.turnFeederOff();
		subsystem.turnShooterOff();
		subsystem.setShooterState(ShooterSubsystem.ShooterState.OFF);
	}

	/**
	 * Finish immediately because this is a state-change command.
	 *
	 * @return true
	 */
	@Override
	protected boolean isFinished() {
		return true;
	}

	/**
	 * Log when this command ends
	 */
	@Override
	protected void end() {
		Logger.addEvent("TurnAllOffWithRequires end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("TurnAllOffWithRequires Interrupted!", this.getClass());
	}
}