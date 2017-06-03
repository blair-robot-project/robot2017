package org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import edu.wpi.first.wpilibj.command.Command;
import org.usfirst.frc.team449.robot.interfaces.subsystem.Intake.IntakeSubsystem;
import org.usfirst.frc.team449.robot.util.Logger;

/**
 * Sets the mode of the intake.
 */
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class SetIntakeMode extends Command {

	/**
	 * The subsystem to execute this command on.
	 */
	private IntakeSubsystem subsystem;

	/**
	 * The mode to set this subsystem to.
	 */
	private IntakeSubsystem.IntakeMode mode;

	/**
	 * Default constructor
	 *
	 * @param subsystem The subsystem to execute this command on.
	 * @param mode      The mode to set the intake to.
	 */
	@JsonCreator
	public SetIntakeMode(@JsonProperty(required = true) IntakeSubsystem subsystem,
	                     @JsonProperty(required = true) IntakeSubsystem.IntakeMode mode) {
		this.subsystem = subsystem;
		this.mode = mode;
	}

	/**
	 * Log when this command is initialized
	 */
	@Override
	protected void initialize() {
		Logger.addEvent("SetIntakeMode init.", this.getClass());
	}

	/**
	 * Set the intake to the given mode.
	 */
	@Override
	protected void execute() {
		subsystem.setMode(mode);
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
		Logger.addEvent("SetIntakeMode end.", this.getClass());
	}

	/**
	 * Log when this command is interrupted.
	 */
	@Override
	protected void interrupted() {
		Logger.addEvent("SetIntakeMode Interrupted!", this.getClass());
	}
}